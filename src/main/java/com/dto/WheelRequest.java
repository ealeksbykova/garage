package com.dto;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WheelRequest {
    String position;
    BigDecimal radiusInch;
    String tireBrand;
    String tireModel;
    String serialNumber;
}