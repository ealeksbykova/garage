"""Bounded loopback-only HTTP benchmark and stress experiment for the lab."""
import argparse
from contextlib import contextmanager
import uuid
import concurrent.futures
import http.client
import json
import math
from pathlib import Path
import socket
import statistics
import subprocess
import tempfile
import threading
import time
from urllib.parse import urlparse

def percentile(values, p):
    values=sorted(values)
    return values[max(0,math.ceil(len(values)*p)-1)] if values else None

@contextmanager
def postgres():
    name='garage-perf-'+uuid.uuid4().hex[:10]
    container=subprocess.check_output(['docker','run','--detach','--rm','--name',name,
        '-e','POSTGRES_DB=garage_perf','-e','POSTGRES_USER=garage',
        '-e','POSTGRES_PASSWORD=garage_perf_local','-p','127.0.0.1::5432','postgres@sha256:2d2b8998d31037bf721cfdf764d76ba74171b4fab3431b7f72c27c56ddbdf9e3'],text=True).strip()
    try:
        for _ in range(120):
            probe=subprocess.run(['docker','exec',container,'pg_isready','-U','garage','-d','garage_perf'],capture_output=True)
            if probe.returncode==0: break
            time.sleep(.5)
        else: raise RuntimeError('PostgreSQL readiness timeout')
        binding=json.loads(subprocess.check_output(['docker','inspect',container],text=True))[0]['NetworkSettings']['Ports']['5432/tcp'][0]
        for _ in range(120):
            try:
                with socket.create_connection(('127.0.0.1',int(binding['HostPort'])),timeout=1): pass
                break
            except OSError: time.sleep(.5)
        else: raise RuntimeError('PostgreSQL forwarded port readiness timeout')
        version=subprocess.check_output(['docker','exec',container,'psql','-U','garage','-d','garage_perf','-Atc','SELECT version()'],text=True).strip()
        digest=subprocess.check_output(['docker','image','inspect','postgres@sha256:2d2b8998d31037bf721cfdf764d76ba74171b4fab3431b7f72c27c56ddbdf9e3','--format','{{json .RepoDigests}}'],text=True).strip()
        yield {'url':'jdbc:postgresql://127.0.0.1:'+binding['HostPort']+'/garage_perf','version':version,'digest':digest}
    finally:
        subprocess.run(['docker','rm','--force',container],stdout=subprocess.DEVNULL,check=False)

def run(jar, output, mode='all'):
    output.mkdir(parents=True,exist_ok=True)
    sock=socket.socket(); sock.bind(('127.0.0.1',0)); port=sock.getsockname()[1]; sock.close()
    with postgres() as database:
        command=['java','-Xms128m','-Xmx512m','-jar',str(jar.resolve()),'--logging.level.root=WARN','--server.address=127.0.0.1',f'--server.port={port}',
          '--spring.datasource.url='+database['url'],'--spring.datasource.username=garage','--spring.datasource.password=garage_perf_local']
        with (output/'server.log').open('w') as log:
            process=subprocess.Popen(command,stdout=log,stderr=subprocess.STDOUT)
            try:
                for _ in range(120):
                    if process.poll() is not None: raise RuntimeError('server stopped; see server.log')
                    try:
                        conn=http.client.HTTPConnection('127.0.0.1',port,timeout=1)
                        conn.request('GET','/api/engines'); response=conn.getresponse(); response.read(); conn.close()
                        if response.status==200: break
                    except (OSError,http.client.HTTPException): pass
                    time.sleep(.5)
                else: raise RuntimeError('server readiness timeout')
                local=threading.local(); counter=0; lock=threading.Lock()
                def request(method,path,body=None):
                    if not hasattr(local,'conn'): local.conn=http.client.HTTPConnection('127.0.0.1',port,timeout=10)
                    start=time.perf_counter()
                    try:
                        local.conn.request(method,'/api/'+path,json.dumps(body) if body is not None else None,{'Content-Type':'application/json'})
                        response=local.conn.getresponse(); data=response.read(); status=response.status
                        parsed=json.loads(data) if data else None
                        expected=201 if method=='POST' else 200
                        valid=status==expected and (isinstance(parsed,list) if method=='GET' else
                            isinstance(parsed,dict) and parsed.get('serialNumber')==body.get('serialNumber') and isinstance(parsed.get('id'),int))
                        error=None if valid else f'status_or_contract:{status}'
                    except (OSError,http.client.HTTPException,ValueError) as ex:
                        status=0; error=type(ex).__name__; local.conn.close(); del local.conn
                    return {'ms':(time.perf_counter()-start)*1000,'status':status,'method':method,'path':path,'error':error}
                def body():
                    nonlocal counter
                    with lock: counter+=1; ident=counter
                    return {'engineType':'PETROL','horsepower':110,'volumeLiters':1.6,'torqueNm':155,'serialNumber':f'PERF-{ident}'}
                def execute(name,clients,iterations=None,seconds=None,write_ratio=0,path='engines'):
                    barrier=threading.Barrier(clients+1); deadline=[0]
                    def worker(index):
                        barrier.wait(); rows=[]; n=0
                        while (n<iterations if iterations is not None else time.perf_counter()<deadline[0]):
                            write=write_ratio==1 or (write_ratio and (n+index)%5==0)
                            rows.append(request('POST' if write else 'GET','engines' if write else path,body() if write else None)); n+=1
                        return rows
                    with concurrent.futures.ThreadPoolExecutor(max_workers=clients) as pool:
                        futures=[pool.submit(worker,i) for i in range(clients)]
                        start=time.perf_counter(); deadline[0]=start+(seconds or 0); barrier.wait()
                        rows=[row for future in futures for row in future.result()]
                        elapsed=time.perf_counter()-start
                    values=[r['ms'] for r in rows]
                    summary={'name':name,'clients':clients,'requests':len(rows),'elapsed_s':elapsed,
                        'rps':len(rows)/elapsed,'mean_ms':statistics.mean(values),
                        'p50_ms':percentile(values,.5),'p95_ms':percentile(values,.95),'p99_ms':percentile(values,.99),'max_ms':max(values),
                        'errors':sum(r['error'] is not None for r in rows),'write_ratio':write_ratio,
                        'counts':{str(code):sum(r['status']==code for r in rows) for code in set(r['status'] for r in rows)}}
                    (output/(name+'.json')).write_text(json.dumps({'summary':summary,'samples':rows},indent=2)+'\n')
                    print(json.dumps(summary),flush=True); return summary
                # Controlled seed size for read-only runs, separate DB per invocation.
                for _ in range(100):
                    row=request('POST','engines',body())
                    if row['error']: raise RuntimeError('fixture creation failed')
                for i in range(100):
                    conn=http.client.HTTPConnection('127.0.0.1',port,timeout=10)
                    payload={'vin':f'PER{i:014d}','brand':'Lab','model':'Bench','productionYear':2024,'mileageKm':0}
                    conn.request('POST','/api/vehicles',json.dumps(payload),{'Content-Type':'application/json'})
                    response=conn.getresponse(); response.read(); conn.close()
                    if response.status!=201: raise RuntimeError('vehicle fixture creation failed')
                for _ in range(100): request('GET','engines')
                summaries=[]
                if mode in ('all','benchmark'):
                    for path in ('engines','vehicles'):
                        for repeat in range(1,4): summaries.append(execute(f'benchmark-{path}-{repeat}',1,iterations=300,path=path))
                    for repeat in range(1,4): summaries.append(execute(f'benchmark-create-{repeat}',1,iterations=300,write_ratio=1))
                if mode in ('all','stress'):
                    summaries.append(execute('stress-baseline',1,iterations=200,path='vehicles'))
                    for clients in (1,8,32,64,128): summaries.append(execute(f'stress-read-{clients}',clients,seconds=5,path='vehicles'))
                    summaries.append(execute('stress-mixed-32',32,seconds=10,write_ratio=.2,path='vehicles'))
                    summaries.append(execute('stress-write-128',128,seconds=8,write_ratio=1))
                    summaries.append(execute('stress-recovery',1,iterations=200,path='vehicles'))
                # Verify the server remains readable and committed writes have no lost/duplicate serials.
                conn=http.client.HTTPConnection('127.0.0.1',port,timeout=10); conn.request('GET','/api/engines')
                response=conn.getresponse(); engines=json.loads(response.read()); conn.close()
                serials=[x['serialNumber'] for x in engines if x['serialNumber'].startswith('PERF-')]
                expected=100+sum(s['counts'].get('201',0) for s in summaries)
                integrity={'expected_created':expected,'actual_created':len(serials),'unique':len(serials)==len(set(serials)),
                    'passed':len(serials)==expected and len(serials)==len(set(serials))}
                final={'timestamp':time.strftime('%Y-%m-%dT%H:%M:%S%z'),'mode':mode,'java_heap':'128m/512m',
                    'server':'127.0.0.1','database':database,'hikari_pool':10,'warmup':100,
                    'fixture_engines':104,'fixture_vehicles':104,'summaries':summaries,'integrity':integrity}
                (output/'results.json').write_text(json.dumps(final,indent=2)+'\n'); print('INTEGRITY',integrity,flush=True)
            finally:
                process.terminate()
                try: process.wait(timeout=15)
                except subprocess.TimeoutExpired: process.kill(); process.wait()

if __name__=='__main__':
    p=argparse.ArgumentParser(); p.add_argument('--jar',type=Path,required=True); p.add_argument('--output',type=Path,required=True)
    p.add_argument('--mode',choices=['benchmark','stress','all'],default='all'); args=p.parse_args(); run(args.jar,args.output,args.mode)
