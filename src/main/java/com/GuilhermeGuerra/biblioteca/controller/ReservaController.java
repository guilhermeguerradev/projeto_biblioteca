package com.GuilhermeGuerra.biblioteca.controller;

import com.GuilhermeGuerra.biblioteca.dto.reserva.CreateReservaRequest;
import com.GuilhermeGuerra.biblioteca.dto.reserva.ReservaResponse;
import com.GuilhermeGuerra.biblioteca.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @GetMapping
    public ResponseEntity<List<ReservaResponse>> findAll() {
        return ResponseEntity.ok().body(reservaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(reservaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ReservaResponse> create(@Valid @RequestBody CreateReservaRequest request) {
        ReservaResponse response = reservaService.create(request);
        URI location = URI.create("/reservas/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponse> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok().body(reservaService.cancelar(id));
    }
}
