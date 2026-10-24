package com.lab;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
class WheelApiTest extends ApiTestBase {
    final String type="wheels";
    @Test @DisplayName("M09-01") void createContract() {
        var body=payload(type); var result=create(type,body);
        assertTrue(result.get("id").asLong()>0); fields(body,result);
    }
    @Test @DisplayName("M09-02") void read() {
        var body=payload(type); long id=create(type,body).get("id").asLong();
        var result=call(HttpMethod.GET,type+"/"+id,null);
        assertEquals(200,result.getStatusCode().value()); assertEquals(id,result.getBody().get("id").asLong()); fields(body,result.getBody());
    }
    @Test @DisplayName("M09-03") void list() {
        long id=create(type); var result=call(HttpMethod.GET,type,null);
        assertEquals(200,result.getStatusCode().value()); assertTrue(result.getBody().isArray());
        boolean found=false; for(var row:result.getBody()) if(row.get("id").asLong()==id) found=true;
        assertTrue(found);
    }
    @Test @DisplayName("M09-04") void update() {
        var body=payload(type); long id=create(type,body).get("id").asLong(); body.put("tireBrand","Pirelli");
        assertEquals(200,call(HttpMethod.PUT,type+"/"+id,body).getStatusCode().value());
        var result=call(HttpMethod.GET,type+"/"+id,null); assertEquals(200,result.getStatusCode().value());
        assertEquals(id,result.getBody().get("id").asLong()); fields(body,result.getBody());
    }
    @Test @DisplayName("M09-05") void delete() {
        long id=create(type); var result=call(HttpMethod.DELETE,type+"/"+id,null);
        assertEquals(204,result.getStatusCode().value()); assertNull(result.getBody());
        assertEquals(404,call(HttpMethod.GET,type+"/"+id,null).getStatusCode().value());
    }
    @Test @DisplayName("M09-06") void missing() { assertEquals(404,call(HttpMethod.GET,type+"/999999999",null).getStatusCode().value()); }
    @Test @DisplayName("M09-07") void invalid() { assertEquals(400,call(HttpMethod.POST,type,Map.of()).getStatusCode().value()); }
}
