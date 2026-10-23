package com.service;

import com.dto.VehicleRequest;
import com.dto.VehicleResponse;
import com.entity.Vehicle;
import com.entity.Wheel;
import com.mapper.VehicleMapper;
import com.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository repository;
    private final EngineRepository engineRepository;
    private final TransmissionRepository transmissionRepository;
    private final OnBoardComputerRepository onBoardComputerRepository;
    private final WheelRepository wheelRepository;
    private final VehicleMapper mapper;

    @Transactional
    public VehicleResponse create(VehicleRequest request) {
        Vehicle vehicle = mapper.toEntity(request);
        applyRelations(vehicle, request);
        return mapper.toResponse(repository.save(vehicle));
    }

    @Transactional(readOnly = true)
    public VehicleResponse findById(Long id) {
        return mapper.toResponse(repository.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found: " + id)));
    }

    @Transactional(readOnly = true)
    public List<VehicleResponse> findAll() {
        return repository.findAllWithDetails().stream().map(mapper::toResponse).toList();
    }

    @Transactional
    public VehicleResponse update(Long id, VehicleRequest request) {
        Vehicle vehicle = repository.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found: " + id));
        mapper.updateEntity(request, vehicle);
        applyRelations(vehicle, request);
        return mapper.toResponse(vehicle);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Vehicle not found: " + id);
        }
        repository.deleteById(id);
    }

    private void applyRelations(Vehicle vehicle, VehicleRequest request) {
        if (request.getEngineId() != null) {
            vehicle.setEngine(engineRepository.findById(request.getEngineId())
                    .orElseThrow(() -> new EntityNotFoundException("Engine not found: " + request.getEngineId())));
        }
        if (request.getTransmissionId() != null) {
            vehicle.setTransmission(transmissionRepository.findById(request.getTransmissionId())
                    .orElseThrow(() -> new EntityNotFoundException("Transmission not found: " + request.getTransmissionId())));
        }
        if (request.getOnBoardComputerId() != null) {
            vehicle.setOnBoardComputer(onBoardComputerRepository.findById(request.getOnBoardComputerId())
                    .orElseThrow(() -> new EntityNotFoundException("OnBoardComputer not found: " + request.getOnBoardComputerId())));
        }
        if (request.getWheelIds() != null) {
            vehicle.getWheels().clear();
            for (Long wheelId : request.getWheelIds()) {
                vehicle.getWheels().add(wheelRepository.findById(wheelId)
                        .orElseThrow(() -> new EntityNotFoundException("Wheel not found: " + wheelId)));
            }
        }
    }
}