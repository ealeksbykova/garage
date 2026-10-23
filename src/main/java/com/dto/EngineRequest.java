package com.dto;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EngineRequest {
    String engineType;
    BigDecimal volumeLiters;
    Integer horsepower;
    Integer torqueNm;
    String serialNumber;
}
