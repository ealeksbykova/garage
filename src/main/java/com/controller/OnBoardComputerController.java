package com.controller;

import com.dto.OnBoardComputerRequest;
import com.dto.OnBoardComputerResponse;
import com.service.OnBoardComputerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/on-board-computers")
@RequiredArgsConstructor
public class OnBoardComputerController {

    private final OnBoardComputerService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OnBoardComputerResponse create(@Valid @RequestBody OnBoardComputerRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public OnBoardComputerResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping
    public List<OnBoardComputerResponse> findAll() {
        return service.findAll();
    }

    @PutMapping("/{id}")
    public OnBoardComputerResponse update(@PathVariable Long id,
                                          @Valid @RequestBody OnBoardComputerRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}