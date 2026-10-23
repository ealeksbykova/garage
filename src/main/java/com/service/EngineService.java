package com.service;

import com.dto.EngineRequest;
import com.dto.EngineResponse;
import com.entity.Engine;
import com.mapper.EngineMapper;
import com.repository.EngineRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EngineService {

    private final EngineRepository repository;
    private final EngineMapper mapper;

    @Transactional
    public EngineResponse create(EngineRequest request) {
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Transactional(readOnly = true)
    public EngineResponse findById(Long id) {
        return repository.findById(id).map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Engine not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<EngineResponse> findAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Transactional
    public EngineResponse update(Long id, EngineRequest request) {
        Engine engine = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Engine not found: " + id));
        mapper.updateEntity(request, engine);
        return mapper.toResponse(engine);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Engine not found: " + id);
        }
        repository.deleteById(id);
    }
}