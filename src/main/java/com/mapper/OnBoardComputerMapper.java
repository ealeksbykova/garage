package com.mapper;

import com.dto.OnBoardComputerRequest;
import com.dto.OnBoardComputerResponse;
import com.entity.OnBoardComputer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OnBoardComputerMapper {
    OnBoardComputer toEntity(OnBoardComputerRequest request);
    OnBoardComputerResponse toResponse(OnBoardComputer entity);
    @Mapping(target = "id", ignore = true)
    void updateEntity(OnBoardComputerRequest request, @MappingTarget OnBoardComputer entity);
}