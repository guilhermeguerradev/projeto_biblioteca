package com.GuilhermeGuerra.biblioteca.controller;

import com.GuilhermeGuerra.biblioteca.dto.emprestimo.CreateEmprestimoRequest;
import com.GuilhermeGuerra.biblioteca.dto.emprestimo.EmprestimoResponse;
import com.GuilhermeGuerra.biblioteca.dto.emprestimo.FinalizarEmprestimoRequest;
import com.GuilhermeGuerra.biblioteca.dto.emprestimo.UpdateEmprestimoRequest;
import com.GuilhermeGuerra.biblioteca.service.EmprestimoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/emprestimos")
@RequiredArgsConstructor
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    @GetMapping
    public ResponseEntity<List<EmprestimoResponse>> findAll() {
        return ResponseEntity.ok().body(emprestimoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmprestimoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(emprestimoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<EmprestimoResponse> create(@Valid @RequestBody CreateEmprestimoRequest request) {
        EmprestimoResponse response = emprestimoService.create(request);
        URI location = URI.create("/emprestimos/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EmprestimoResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateEmprestimoRequest request) {
        return ResponseEntity.ok().body(emprestimoService.update(id, request));
    }

    @PatchMapping("/{id}/renovar")
    public ResponseEntity<EmprestimoResponse> renovarEmprestimo(@PathVariable Long id) {
        return ResponseEntity.ok().body(emprestimoService.realizarRenovacao(id));
    }

    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<EmprestimoResponse> finalizarEmprestimo(@PathVariable Long id, @Valid @RequestBody FinalizarEmprestimoRequest request) {
        return ResponseEntity.ok().body(emprestimoService.finalizarEmprestimo(id, request));
    }


}
