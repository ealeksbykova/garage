package com.lab;
import com.Main;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.containers.PostgreSQLContainer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes=Main.class, webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class ApiTestBase {
    private static final AtomicLong sequence = new AtomicLong();
    private static class Database {
        static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(DockerImageName.parse("postgres@sha256:2d2b8998d31037bf721cfdf764d76ba74171b4fab3431b7f72c27c56ddbdf9e3").asCompatibleSubstituteFor("postgres"))
            .withDatabaseName("garage_test").withUsername("garage").withPassword("garage_test_local");
        static {
            POSTGRES.start();
            // Colima may publish the forwarded host port after container readiness.
            RuntimeException last = null;
            for (int attempt = 0; attempt < 60; attempt++) {
                try (var connection = java.sql.DriverManager.getConnection(
                        POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())) {
                    last = null;
                    break;
                } catch (java.sql.SQLException exception) {
                    last = new IllegalStateException("PostgreSQL host port is not ready", exception);
                    try { Thread.sleep(500); }
                    catch (InterruptedException interrupted) {
                        Thread.currentThread().interrupt(); throw new IllegalStateException(interrupted);
                    }
                }
            }
            if (last != null) throw last;
        }
    }
    @Autowired protected TestRestTemplate http;
    @Autowired protected ObjectMapper json;
    @Autowired protected JdbcTemplate jdbc;
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", Database.POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", Database.POSTGRES::getUsername);
        registry.add("spring.datasource.password", Database.POSTGRES::getPassword);
    }
    static String unique() { return Long.toString(sequence.incrementAndGet()); }
    static Map<String,Object> payload(String type) {
        var result = new LinkedHashMap<String,Object>();
        switch (type) {
            case "engines" -> result.putAll(Map.of("engineType","PETROL","volumeLiters",1.6,
                "horsepower",110,"torqueNm",155,"serialNumber","ENG-LAB-"+unique()));
            case "transmissions" -> result.putAll(Map.of("transmissionType","MANUAL",
                "gearsCount",5,"serialNumber","TRN-LAB-"+unique()));
            case "on-board-computers" -> result.putAll(Map.of("firmwareVersion","v1.0",
                "cpuModel","ARM Lab","ramMb",512));
            case "wheels" -> result.putAll(Map.of("position","FRONT_LEFT","radiusInch",16.0,
                "tireBrand","Michelin","tireModel","Primacy 4","serialNumber","WHL-LAB-"+unique()));
            case "vehicles" -> result.putAll(Map.of("vin",String.format("LAB%014d",sequence.incrementAndGet()),
                "brand","Volkswagen","model","Golf","productionYear",2020,"color","Black","mileageKm",45000));
            default -> throw new IllegalArgumentException(type);
        }
        return result;
    }
    ResponseEntity<JsonNode> call(HttpMethod method, String path, Object body) {
        var headers = new HttpHeaders(); headers.setContentType(MediaType.APPLICATION_JSON);
        return http.exchange("/api/"+path, method, new HttpEntity<>(body,headers), JsonNode.class);
    }
    JsonNode create(String type, Map<String,Object> body) {
        var response = call(HttpMethod.POST, type, body);
        assertEquals(201,response.getStatusCode().value(), response.toString());
        return response.getBody();
    }
    long create(String type) { return create(type,payload(type)).get("id").asLong(); }
    void fields(Map<String,Object> expected, JsonNode actual) {
        expected.forEach((key,value)-> {
            assertTrue(actual.has(key), "missing field "+key);
            if (value instanceof Number n) assertEquals(n.doubleValue(), actual.get(key).asDouble(),0.000001,key);
            else assertEquals(value.toString(),actual.get(key).asText(),key);
        });
    }
}
