package com.GuilhermeGuerra.biblioteca.controller;

import com.GuilhermeGuerra.biblioteca.dto.exemplar.CreateExemplarRequest;
import com.GuilhermeGuerra.biblioteca.dto.exemplar.ExemplarResponse;
import com.GuilhermeGuerra.biblioteca.dto.exemplar.UpdateExemplarRequest;
import com.GuilhermeGuerra.biblioteca.service.ExemplarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/exemplares")
@RequiredArgsConstructor
public class ExemplarController {

    private final ExemplarService exemplarService;

    @GetMapping
    public ResponseEntity<List<ExemplarResponse>> findAll() {
        return ResponseEntity.ok().body(exemplarService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExemplarResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(exemplarService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ExemplarResponse> create(@Valid @RequestBody CreateExemplarRequest request) {
        ExemplarResponse response = exemplarService.create(request);
        URI location = URI.create("/exemplares/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ExemplarResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateExemplarRequest request) {
        return ResponseEntity.ok().body(exemplarService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> descartar(@PathVariable Long id) {
        exemplarService.descartar(id);
        return ResponseEntity.noContent().build();
    }
}
