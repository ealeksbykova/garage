package com.controller;

import com.dto.EngineRequest;
import com.dto.EngineResponse;
import com.service.EngineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/engines")
@RequiredArgsConstructor
public class EngineController {

    private final EngineService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EngineResponse create(@Valid @RequestBody EngineRequest r) { return service.create(r); }

    @GetMapping("/{id}")
    public EngineResponse findById(@PathVariable Long id) { return service.findById(id); }

    @GetMapping
    public List<EngineResponse> findAll() { return service.findAll(); }

    @PutMapping("/{id}")
    public EngineResponse update(@PathVariable Long id, @Valid @RequestBody EngineRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}