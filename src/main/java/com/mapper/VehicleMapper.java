package com.mapper;

import com.dto.VehicleRequest;
import com.dto.VehicleResponse;
import com.entity.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {
        EngineMapper.class, TransmissionMapper.class,
        OnBoardComputerMapper.class, WheelMapper.class
})
public interface VehicleMapper {

    @Mapping(target = "engine", ignore = true)
    @Mapping(target = "transmission", ignore = true)
    @Mapping(target = "onBoardComputer", ignore = true)
    @Mapping(target = "wheels", ignore = true)
    Vehicle toEntity(VehicleRequest request);

    VehicleResponse toResponse(Vehicle vehicle);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "engine", ignore = true)
    @Mapping(target = "transmission", ignore = true)
    @Mapping(target = "onBoardComputer", ignore = true)
    @Mapping(target = "wheels", ignore = true)
    void updateEntity(VehicleRequest request, @MappingTarget Vehicle vehicle);
}