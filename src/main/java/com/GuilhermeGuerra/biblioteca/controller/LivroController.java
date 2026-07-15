package com.GuilhermeGuerra.biblioteca.controller;

import com.GuilhermeGuerra.biblioteca.dto.livro.CreateLivroRequest;
import com.GuilhermeGuerra.biblioteca.dto.livro.LivroResponse;
import com.GuilhermeGuerra.biblioteca.dto.livro.UpdateLivroRequest;
import com.GuilhermeGuerra.biblioteca.service.LivroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/livros")
@RequiredArgsConstructor
public class LivroController {

    private final LivroService livroService;

    @GetMapping
    public ResponseEntity<List<LivroResponse>> findAll() {
        return ResponseEntity.ok().body(livroService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LivroResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(livroService.findById(id));
    }

    @PostMapping
    public ResponseEntity<LivroResponse> create(@Valid @RequestBody CreateLivroRequest request) {
        LivroResponse response = livroService.create(request);
        URI location = URI.create("/livros/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<LivroResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateLivroRequest request) {
        return ResponseEntity.ok().body(livroService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        livroService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
