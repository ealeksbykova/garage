package com.mapper;

import com.dto.EngineRequest;
import com.dto.EngineResponse;
import com.entity.Engine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EngineMapper {
    Engine toEntity(EngineRequest request);
    EngineResponse toResponse(Engine engine);

    @Mapping(target = "id", ignore = true)
    void updateEntity(EngineRequest request, @MappingTarget Engine engine);

    default Engine.EngineType mapEngineType(String value) {
        return value != null ? Engine.EngineType.valueOf(value) : null;
    }
    default String mapEngineType(Engine.EngineType value) {
        return value != null ? value.name() : null;
    }
}