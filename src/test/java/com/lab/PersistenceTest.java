package com.lab;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
class PersistenceTest extends ApiTestBase {
    @Test @DisplayName("M13-01") void schema() {
        for(String table:List.of("engine","transmission","on_board_computer","wheel","vehicle","vehicle_wheel"))
            assertEquals(1,jdbc.queryForObject("SELECT count(*) FROM information_schema.tables WHERE table_schema='public' AND table_name=?",Integer.class,table));
        assertEquals(14,jdbc.queryForObject("SELECT count(*) FROM DATABASECHANGELOG",Integer.class));
    }
    @Test @DisplayName("M13-02") void seedDates() {
        var result=call(HttpMethod.GET,"vehicles/1",null); assertEquals(200,result.getStatusCode().value());
        var value=result.getBody(); var expected=LocalDateTime.of(2024,1,15,10,0);
        assertEquals(expected,LocalDateTime.parse(value.get("createdAt").asText()));
        assertEquals(expected,LocalDateTime.parse(value.get("onBoardComputer").get("installedAt").asText()));
        assertEquals(4,value.get("wheels").size());
    }
    @Test @DisplayName("M13-03") void foreignKeys() {
        org.junit.jupiter.api.Assertions.assertThrows(org.springframework.dao.DataIntegrityViolationException.class,
            () -> jdbc.update("INSERT INTO vehicle_wheel(vehicle_id,wheel_id) VALUES (-1,-1)"));
        assertEquals(0,jdbc.queryForObject("SELECT count(*) FROM vehicle_wheel WHERE vehicle_id=-1",Integer.class));
    }
    @Test @DisplayName("M13-04") void commit() {
        var body=payload("engines"); long id=create("engines",body).get("id").asLong();
        var row=jdbc.queryForMap("SELECT serial_number,horsepower FROM engine WHERE id=?",id);
        assertEquals(body.get("serialNumber"),row.get("serial_number")); assertEquals(110,((Number)row.get("horsepower")).intValue());
    }
    @Test @DisplayName("M13-05") void identityAfterSeed() {
        for (String type:List.of("engines","transmissions","on-board-computers","wheels","vehicles")) {
            long seedMax = type.equals("wheels") ? 12 : type.equals("on-board-computers") ? 3 : 4;
            assertTrue(create(type)>seedMax,type);
        }
    }
    @org.springframework.beans.factory.annotation.Autowired liquibase.integration.spring.SpringLiquibase liquibase;
    @Test @DisplayName("M13-06") void migrationRepeat() throws liquibase.exception.LiquibaseException {
        int changes=jdbc.queryForObject("SELECT count(*) FROM databasechangelog",Integer.class);
        int vehicles=jdbc.queryForObject("SELECT count(*) FROM vehicle",Integer.class);
        liquibase.afterPropertiesSet();
        assertEquals(changes,jdbc.queryForObject("SELECT count(*) FROM databasechangelog",Integer.class));
        assertEquals(vehicles,jdbc.queryForObject("SELECT count(*) FROM vehicle",Integer.class));
    }
    @Test @DisplayName("M13-07") void nativeTimestampType() {
        assertEquals("timestamp without time zone",jdbc.queryForObject(
            "SELECT data_type FROM information_schema.columns WHERE table_schema='public' AND table_name='vehicle' AND column_name='created_at'",String.class));
    }
}
