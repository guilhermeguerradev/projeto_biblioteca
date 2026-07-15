package com.GuilhermeGuerra.biblioteca.controller;

import com.GuilhermeGuerra.biblioteca.dto.multa.MultaResponse;
import com.GuilhermeGuerra.biblioteca.service.MultaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/multas")
@RequiredArgsConstructor
public class MultaController {

    private final MultaService multaService;

    @GetMapping
    public ResponseEntity<List<MultaResponse>> findAll() {
        return ResponseEntity.ok().body(multaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MultaResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(multaService.findById(id));
    }

    @PatchMapping("/{id}/pagar")
    public ResponseEntity<MultaResponse> pagarMulta(@PathVariable Long id) {
        return ResponseEntity.ok().body(multaService.pagarMulta(id));
    }
}
