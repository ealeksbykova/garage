package com.controller;

import com.dto.TransmissionRequest;
import com.dto.TransmissionResponse;
import com.service.TransmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transmissions")
@RequiredArgsConstructor
public class TransmissionController {

    private final TransmissionService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransmissionResponse create(@Valid @RequestBody TransmissionRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public TransmissionResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping
    public List<TransmissionResponse> findAll() {
        return service.findAll();
    }

    @PutMapping("/{id}")
    public TransmissionResponse update(@PathVariable Long id,
                                       @Valid @RequestBody TransmissionRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}