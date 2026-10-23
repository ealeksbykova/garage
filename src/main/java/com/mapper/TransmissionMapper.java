package com.mapper;

import com.dto.TransmissionRequest;
import com.dto.TransmissionResponse;
import com.entity.Transmission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TransmissionMapper {
    Transmission toEntity(TransmissionRequest request);
    TransmissionResponse toResponse(Transmission entity);
    @Mapping(target = "id", ignore = true)
    void updateEntity(TransmissionRequest request, @MappingTarget Transmission entity);

    default Transmission.TransmissionType map(String value) {
        return value != null ? Transmission.TransmissionType.valueOf(value) : null;
    }
    default String map(Transmission.TransmissionType value) {
        return value != null ? value.name() : null;
    }
}