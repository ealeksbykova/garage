package com.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EngineRequest {
    @NotBlank
    @Pattern(regexp = "PETROL|DIESEL|ELECTRIC|HYBRID")
    String engineType;
    @PositiveOrZero
    BigDecimal volumeLiters;
    @NotNull
    @Positive
    Integer horsepower;
    @PositiveOrZero
    Integer torqueNm;
    @NotBlank
    @Size(max = 64)
    String serialNumber;
}
