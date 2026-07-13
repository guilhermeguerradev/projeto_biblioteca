package com.GuilhermeGuerra.biblioteca.controller;

import com.GuilhermeGuerra.biblioteca.dto.colaborador.ColaboradorResponse;
import com.GuilhermeGuerra.biblioteca.dto.colaborador.CreateColaboradorRequest;
import com.GuilhermeGuerra.biblioteca.dto.colaborador.FilterColaboradorRequest;
import com.GuilhermeGuerra.biblioteca.dto.colaborador.UpdateColaboradorRequest;
import com.GuilhermeGuerra.biblioteca.service.ColaboradorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/colaboradores")
@RequiredArgsConstructor
public class ColaboradorController {

    private final ColaboradorService colaboradorService;

    @GetMapping
    public ResponseEntity<Page<ColaboradorResponse>> listar(FilterColaboradorRequest filter,
                                                            @PageableDefault(size = 20, sort = "usuario.nome") Pageable pageable) {
        return ResponseEntity.ok().body(colaboradorService.listarComFiltros(filter, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ColaboradorResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(colaboradorService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ColaboradorResponse> create(@Valid @RequestBody CreateColaboradorRequest request) {
        ColaboradorResponse response = colaboradorService.create(request);
        URI location = URI.create("/colaboradores/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ColaboradorResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateColaboradorRequest request) {
        return ResponseEntity.ok().body(colaboradorService.update(id,request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        colaboradorService.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
