package com.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import lombok.*;
import java.util.List;

@Builder @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class VehicleRequest {
    @NotBlank
    @Pattern(regexp = "[A-HJ-NPR-Z0-9]{17}")
    String vin;
    @NotBlank
    @Size(max = 100)
    String brand;
    @NotBlank
    @Size(max = 100)
    String model;
    @NotNull
    @Min(1886)
    Integer productionYear;
    @Size(max = 50)
    String color;
    @NotNull
    @PositiveOrZero
    Long mileageKm;

    @Positive
    Long engineId;
    @Positive
    Long transmissionId;
    @Positive
    Long onBoardComputerId;
    List<Long> wheelIds;
}
