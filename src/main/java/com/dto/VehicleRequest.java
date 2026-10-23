package com.dto;

import lombok.*;
import java.util.List;

@Builder @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class VehicleRequest {
    String vin;
    String brand;
    String model;
    Integer productionYear;
    String color;
    Long mileageKm;

    Long engineId;
    Long transmissionId;
    Long onBoardComputerId;
    List<Long> wheelIds;
}