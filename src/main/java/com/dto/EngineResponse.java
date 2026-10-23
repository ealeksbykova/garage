package com.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EngineResponse {
    Long id;
    String engineType;
    BigDecimal volumeLiters;
    Integer horsepower;
    Integer torqueNm;
    String serialNumber;
}