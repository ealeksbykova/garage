package com.controller;

import com.dto.WheelRequest;
import com.dto.WheelResponse;
import com.service.WheelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wheels")
@RequiredArgsConstructor
public class WheelController {

    private final WheelService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WheelResponse create(@Valid @RequestBody WheelRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public WheelResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping
    public List<WheelResponse> findAll() {
        return service.findAll();
    }

    @PutMapping("/{id}")
    public WheelResponse update(@PathVariable Long id,
                                @Valid @RequestBody WheelRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}