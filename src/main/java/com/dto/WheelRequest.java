package com.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WheelRequest {
    @NotBlank
    @Pattern(regexp = "FRONT_LEFT|FRONT_RIGHT|REAR_LEFT|REAR_RIGHT")
    String position;
    @NotNull
    @Positive
    BigDecimal radiusInch;
    @Size(max = 100)
    String tireBrand;
    @Size(max = 100)
    String tireModel;
    @NotBlank
    @Size(max = 64)
    String serialNumber;
}
