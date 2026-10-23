package com.service;

import com.dto.WheelRequest;
import com.dto.WheelResponse;
import com.entity.Wheel;
import com.mapper.WheelMapper;
import com.repository.WheelRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WheelService {

    private final WheelRepository repository;
    private final WheelMapper mapper;

    @Transactional
    public WheelResponse create(WheelRequest request) {
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Transactional(readOnly = true)
    public WheelResponse findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Wheel not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<WheelResponse> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public WheelResponse update(Long id, WheelRequest request) {
        Wheel entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Wheel not found: " + id));
        mapper.updateEntity(request, entity);
        return mapper.toResponse(entity);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Wheel not found: " + id);
        }
        repository.deleteById(id);
    }
}