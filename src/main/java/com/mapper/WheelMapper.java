package com.mapper;

import com.dto.WheelRequest;
import com.dto.WheelResponse;
import com.entity.Wheel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WheelMapper {
    Wheel toEntity(WheelRequest request);
    WheelResponse toResponse(Wheel entity);
    @Mapping(target = "id", ignore = true)
    void updateEntity(WheelRequest request, @MappingTarget Wheel entity);

    default Wheel.Position map(String value) {
        return value != null ? Wheel.Position.valueOf(value) : null;
    }
    default String map(Wheel.Position value) {
        return value != null ? value.name() : null;
    }
}