package com.GuilhermeGuerra.biblioteca.controller;

import com.GuilhermeGuerra.biblioteca.dto.aluno.AlunoResponse;
import com.GuilhermeGuerra.biblioteca.dto.aluno.CreateAlunoRequest;
import com.GuilhermeGuerra.biblioteca.dto.aluno.FilterAlunoRequest;
import com.GuilhermeGuerra.biblioteca.dto.aluno.UpdateAlunoRequest;
import com.GuilhermeGuerra.biblioteca.service.AlunoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/alunos")
@RequiredArgsConstructor
public class AlunoController {

    private final AlunoService alunoService;

    @GetMapping
    public ResponseEntity<Page<AlunoResponse>> listar(
            FilterAlunoRequest filter,
            @PageableDefault(size = 20, sort = "usuario.nome") Pageable pageable) {
        return ResponseEntity.ok().body(alunoService.listarComFiltros(filter, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlunoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(alunoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<AlunoResponse> create(@Valid @RequestBody CreateAlunoRequest request) {
        AlunoResponse aluno = alunoService.create(request);
        URI location = URI.create("/alunos/" + aluno.id());
        return ResponseEntity.created(location).body(aluno);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AlunoResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateAlunoRequest request) {
        return ResponseEntity.ok().body(alunoService.update(id,request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        alunoService.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
