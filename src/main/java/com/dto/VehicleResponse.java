package com.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Builder @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class VehicleResponse {
    Long id;
    String vin;
    String brand;
    String model;
    Integer productionYear;
    String color;
    Long mileageKm;
    LocalDateTime createdAt;
    EngineResponse engine;
    TransmissionResponse transmission;
    OnBoardComputerResponse onBoardComputer;
    List<WheelResponse> wheels;
}