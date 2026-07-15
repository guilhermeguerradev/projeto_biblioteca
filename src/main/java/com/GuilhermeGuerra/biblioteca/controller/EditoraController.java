package com.GuilhermeGuerra.biblioteca.controller;

import com.GuilhermeGuerra.biblioteca.dto.editora.CreateEditoraRequest;
import com.GuilhermeGuerra.biblioteca.dto.editora.EditoraResponse;
import com.GuilhermeGuerra.biblioteca.dto.editora.UpdateEditoraRequest;
import com.GuilhermeGuerra.biblioteca.service.EditoraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/editoras")
@RequiredArgsConstructor
public class EditoraController {

    private final EditoraService editoraService;

    @GetMapping
    public ResponseEntity<List<EditoraResponse>> findAll() {
        return ResponseEntity.ok().body(editoraService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EditoraResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(editoraService.findById(id));
    }

    @PostMapping
    public ResponseEntity<EditoraResponse> create(@Valid @RequestBody CreateEditoraRequest request) {
        EditoraResponse response = editoraService.create(request);
        URI location = URI.create("/editoras/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EditoraResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateEditoraRequest request) {
        return ResponseEntity.ok().body(editoraService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        editoraService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
