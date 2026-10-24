package com.lab;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
class RelationsApiTest extends ApiTestBase {
    @Test @DisplayName("M12-01") void assemble() {
        long engine=create("engines"), transmission=create("transmissions"), computer=create("on-board-computers"), wheel=create("wheels");
        var body=payload("vehicles"); body.put("engineId",engine); body.put("transmissionId",transmission);
        body.put("onBoardComputerId",computer); body.put("wheelIds",List.of(wheel));
        long id=create("vehicles",body).get("id").asLong(); var result=call(HttpMethod.GET,"vehicles/"+id,null);
        assertEquals(200,result.getStatusCode().value()); var value=result.getBody();
        assertEquals(engine,value.get("engine").get("id").asLong());
        assertEquals(transmission,value.get("transmission").get("id").asLong());
        assertEquals(computer,value.get("onBoardComputer").get("id").asLong());
        assertEquals(1,value.get("wheels").size()); assertEquals(wheel,value.get("wheels").get(0).get("id").asLong());
    }
    @Test @DisplayName("M12-02") void missingRelation() {
        var body=payload("vehicles"); body.put("engineId",999999999L);
        assertEquals(404,call(HttpMethod.POST,"vehicles",body).getStatusCode().value());
        assertEquals(0,jdbc.queryForObject("SELECT count(*) FROM vehicle WHERE vin=?",Integer.class,body.get("vin")));
    }
    @Test @DisplayName("M12-03") void exclusiveEngine() {
        long engine=create("engines"); var first=payload("vehicles"); first.put("engineId",engine);
        long owner=create("vehicles",first).get("id").asLong(); var second=payload("vehicles"); second.put("engineId",engine);
        assertEquals(409,call(HttpMethod.POST,"vehicles",second).getStatusCode().value());
        assertEquals(owner,jdbc.queryForObject("SELECT id FROM vehicle WHERE engine_id=?",Long.class,engine));
    }
    @Test @DisplayName("M12-04") void replaceWheels() {
        long first=create("wheels"), second=create("wheels"); var body=payload("vehicles"); body.put("wheelIds",List.of(first));
        long id=create("vehicles",body).get("id").asLong(); body.put("wheelIds",List.of(second));
        assertEquals(200,call(HttpMethod.PUT,"vehicles/"+id,body).getStatusCode().value());
        var wheels=call(HttpMethod.GET,"vehicles/"+id,null).getBody().get("wheels");
        assertEquals(1,wheels.size()); assertEquals(second,wheels.get(0).get("id").asLong());
        assertEquals(0,jdbc.queryForObject("SELECT count(*) FROM vehicle_wheel WHERE wheel_id=?",Integer.class,first));
    }
    @Test @DisplayName("M12-05") void rollback() {
        long wheel=create("wheels"); var body=payload("vehicles"); body.put("wheelIds",List.of(wheel));
        long id=create("vehicles",body).get("id").asLong(); body.put("brand","RollbackSentinel"); body.put("wheelIds",List.of(999999999L));
        assertEquals(404,call(HttpMethod.PUT,"vehicles/"+id,body).getStatusCode().value());
        var value=call(HttpMethod.GET,"vehicles/"+id,null).getBody();
        assertEquals("Volkswagen",value.get("brand").asText()); assertEquals(wheel,value.get("wheels").get(0).get("id").asLong());
    }
    @Test @DisplayName("M12-06") void deleteComponent() {
        long engine=create("engines"); var body=payload("vehicles"); body.put("engineId",engine); long id=create("vehicles",body).get("id").asLong();
        assertEquals(204,call(HttpMethod.DELETE,"engines/"+engine,null).getStatusCode().value());
        var value=call(HttpMethod.GET,"vehicles/"+id,null); assertEquals(200,value.getStatusCode().value()); assertTrue(value.getBody().get("engine").isNull());
    }
    @Test @DisplayName("M12-07") void deleteVehicle() {
        long wheel=create("wheels"); var body=payload("vehicles"); body.put("wheelIds",List.of(wheel)); long id=create("vehicles",body).get("id").asLong();
        assertEquals(204,call(HttpMethod.DELETE,"vehicles/"+id,null).getStatusCode().value());
        assertEquals(0,jdbc.queryForObject("SELECT count(*) FROM vehicle_wheel WHERE vehicle_id=?",Integer.class,id));
        assertEquals(200,call(HttpMethod.GET,"wheels/"+wheel,null).getStatusCode().value());
    }
    @Test @DisplayName("M12-08") void clearWheels() {
        long wheel=create("wheels"); var body=payload("vehicles"); body.put("wheelIds",List.of(wheel)); long id=create("vehicles",body).get("id").asLong();
        body.put("wheelIds",List.of()); assertEquals(200,call(HttpMethod.PUT,"vehicles/"+id,body).getStatusCode().value());
        assertEquals(0,call(HttpMethod.GET,"vehicles/"+id,null).getBody().get("wheels").size());
        assertEquals(0,jdbc.queryForObject("SELECT count(*) FROM vehicle_wheel WHERE vehicle_id=?",Integer.class,id));
    }
}
