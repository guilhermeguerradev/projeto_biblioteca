package com.GuilhermeGuerra.biblioteca.controller;

import com.GuilhermeGuerra.biblioteca.dto.professor.CreateProfessorRequest;
import com.GuilhermeGuerra.biblioteca.dto.professor.ProfessorResponse;
import com.GuilhermeGuerra.biblioteca.dto.professor.UpdateProfessorRequest;
import com.GuilhermeGuerra.biblioteca.service.ProfessorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/professores")
@RequiredArgsConstructor
public class ProfessorController {

    private final ProfessorService professorService;

    @GetMapping
    public ResponseEntity<List<ProfessorResponse>> findAll() {
        return ResponseEntity.ok().body(professorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfessorResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(professorService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProfessorResponse> create(@Valid @RequestBody CreateProfessorRequest request) {
        ProfessorResponse response = professorService.create(request);
        URI location = URI.create("/professores/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProfessorResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateProfessorRequest request) {
        return ResponseEntity.ok().body(professorService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        professorService.desativar(id);
        return ResponseEntity.noContent().build();
    }


}
