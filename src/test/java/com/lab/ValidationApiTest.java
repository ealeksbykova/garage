package com.lab;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import static org.junit.jupiter.api.Assertions.assertEquals;
class ValidationApiTest extends ApiTestBase {
@Test @DisplayName("M11-01") void negativePower() {
        long before=jdbc.queryForObject("SELECT count(*) FROM engine",Long.class);
        var body=payload("engines");  body.put("horsepower",-1);
        var result=call(HttpMethod.POST,"engines",body); assertEquals(400,result.getStatusCode().value()); assertEquals(before,jdbc.queryForObject("SELECT count(*) FROM engine",Long.class));
    }
@Test @DisplayName("M11-02") void unknownEngine() {
        long before=jdbc.queryForObject("SELECT count(*) FROM engine",Long.class);
        var body=payload("engines");  body.put("engineType","STEAM");
        var result=call(HttpMethod.POST,"engines",body); assertEquals(400,result.getStatusCode().value()); assertEquals(before,jdbc.queryForObject("SELECT count(*) FROM engine",Long.class));
    }
@Test @DisplayName("M11-03") void badVin() {
        long before=jdbc.queryForObject("SELECT count(*) FROM vehicle",Long.class);
        var body=payload("vehicles");  body.put("vin","SHORT");
        var result=call(HttpMethod.POST,"vehicles",body); assertEquals(400,result.getStatusCode().value()); assertEquals(before,jdbc.queryForObject("SELECT count(*) FROM vehicle",Long.class));
    }
@Test @DisplayName("M11-04") void blankBrand() {
        long before=jdbc.queryForObject("SELECT count(*) FROM vehicle",Long.class);
        var body=payload("vehicles");  body.put("brand","  ");
        var result=call(HttpMethod.POST,"vehicles",body); assertEquals(400,result.getStatusCode().value()); assertEquals(before,jdbc.queryForObject("SELECT count(*) FROM vehicle",Long.class));
    }
@Test @DisplayName("M11-05") void negativeMileage() {
        long before=jdbc.queryForObject("SELECT count(*) FROM vehicle",Long.class);
        var body=payload("vehicles");  body.put("mileageKm",-1);
        var result=call(HttpMethod.POST,"vehicles",body); assertEquals(400,result.getStatusCode().value()); assertEquals(before,jdbc.queryForObject("SELECT count(*) FROM vehicle",Long.class));
    }
@Test @DisplayName("M11-06") void negativeGears() {
        long before=jdbc.queryForObject("SELECT count(*) FROM transmission",Long.class);
        var body=payload("transmissions");  body.put("gearsCount",-1);
        var result=call(HttpMethod.POST,"transmissions",body); assertEquals(400,result.getStatusCode().value()); assertEquals(before,jdbc.queryForObject("SELECT count(*) FROM transmission",Long.class));
    }
@Test @DisplayName("M11-07") void zeroRadius() {
        long before=jdbc.queryForObject("SELECT count(*) FROM wheel",Long.class);
        var body=payload("wheels");  body.put("radiusInch",0);
        var result=call(HttpMethod.POST,"wheels",body); assertEquals(400,result.getStatusCode().value()); assertEquals(before,jdbc.queryForObject("SELECT count(*) FROM wheel",Long.class));
    }
@Test @DisplayName("M11-08") void negativeRam() {
        long before=jdbc.queryForObject("SELECT count(*) FROM on_board_computer",Long.class);
        var body=payload("on-board-computers");  body.put("ramMb",-1);
        var result=call(HttpMethod.POST,"on-board-computers",body); assertEquals(400,result.getStatusCode().value()); assertEquals(before,jdbc.queryForObject("SELECT count(*) FROM on_board_computer",Long.class));
    }
@Test @DisplayName("M11-09") void zeroElectricVolume() {
        long before=jdbc.queryForObject("SELECT count(*) FROM engine",Long.class);
        var body=payload("engines"); body.put("engineType","ELECTRIC"); body.put("volumeLiters",0);
        var result=call(HttpMethod.POST,"engines",body); assertEquals(201,result.getStatusCode().value()); assertEquals(0,result.getBody().get("volumeLiters").asInt());
    }
@Test @DisplayName("M11-10") void zeroCvtGears() {
        long before=jdbc.queryForObject("SELECT count(*) FROM transmission",Long.class);
        var body=payload("transmissions"); body.put("transmissionType","CVT"); body.put("gearsCount",0);
        var result=call(HttpMethod.POST,"transmissions",body); assertEquals(201,result.getStatusCode().value()); assertEquals(0,result.getBody().get("gearsCount").asInt());
    }
@Test @DisplayName("M11-11") void duplicate() {
    var body=payload("engines"); create("engines",body);
    long count=jdbc.queryForObject("SELECT count(*) FROM engine",Long.class);
    assertEquals(409,call(HttpMethod.POST,"engines",body).getStatusCode().value());
    assertEquals(count,jdbc.queryForObject("SELECT count(*) FROM engine",Long.class));
}
@Test @DisplayName("M11-12") void malformed() {
    assertEquals(400,call(HttpMethod.POST,"engines","{\"engineType\":").getStatusCode().value());
}
    @Test @DisplayName("M11-13") void unsupportedContentType() {
        var headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.TEXT_PLAIN);
        var response = http.exchange("/api/engines", HttpMethod.POST,
            new org.springframework.http.HttpEntity<>("not json", headers), String.class);
        assertEquals(415,response.getStatusCode().value());
    }
}
