package com.service;

import com.dto.TransmissionRequest;
import com.dto.TransmissionResponse;
import com.entity.Transmission;
import com.mapper.TransmissionMapper;
import com.repository.TransmissionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransmissionService {

    private final TransmissionRepository repository;
    private final TransmissionMapper mapper;

    @Transactional
    public TransmissionResponse create(TransmissionRequest request) {
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Transactional(readOnly = true)
    public TransmissionResponse findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Transmission not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<TransmissionResponse> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public TransmissionResponse update(Long id, TransmissionRequest request) {
        Transmission entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transmission not found: " + id));
        mapper.updateEntity(request, entity);
        return mapper.toResponse(entity);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Transmission not found: " + id);
        }
        repository.deleteById(id);
    }
}