package com.service;

import com.dto.OnBoardComputerRequest;
import com.dto.OnBoardComputerResponse;
import com.entity.OnBoardComputer;
import com.mapper.OnBoardComputerMapper;
import com.repository.OnBoardComputerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OnBoardComputerService {

    private final OnBoardComputerRepository repository;
    private final OnBoardComputerMapper mapper;

    @Transactional
    public OnBoardComputerResponse create(OnBoardComputerRequest request) {
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Transactional(readOnly = true)
    public OnBoardComputerResponse findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("OnBoardComputer not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<OnBoardComputerResponse> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public OnBoardComputerResponse update(Long id, OnBoardComputerRequest request) {
        OnBoardComputer entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("OnBoardComputer not found: " + id));
        mapper.updateEntity(request, entity);
        return mapper.toResponse(entity);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("OnBoardComputer not found: " + id);
        }
        repository.deleteById(id);
    }
}