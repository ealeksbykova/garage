package com.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WheelResponse {
    Long id;
    String position;
    BigDecimal radiusInch;
    String tireBrand;
    String tireModel;
    String serialNumber;
    LocalDateTime installedAt;
}