package com.GuilhermeGuerra.biblioteca.controller;


import com.GuilhermeGuerra.biblioteca.dto.autor.AutorResponse;
import com.GuilhermeGuerra.biblioteca.dto.autor.CreateAutorRequest;
import com.GuilhermeGuerra.biblioteca.dto.autor.UpdateAutorRequest;
import com.GuilhermeGuerra.biblioteca.service.AutorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/autores")
@RequiredArgsConstructor
public class AutorController {

    private final AutorService autorService;

    @GetMapping
    public ResponseEntity<List<AutorResponse>> findAll() {
        return ResponseEntity.ok().body(autorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AutorResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(autorService.findById(id));
    }

    @PostMapping
    public ResponseEntity<AutorResponse> create(@Valid @RequestBody CreateAutorRequest request) {
        AutorResponse response = autorService.create(request);
        URI location = URI.create("/autores/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AutorResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateAutorRequest request) {
        return ResponseEntity.ok().body(autorService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        autorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
