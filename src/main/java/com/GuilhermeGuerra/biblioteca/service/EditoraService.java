package com.GuilhermeGuerra.biblioteca.service;

import com.GuilhermeGuerra.biblioteca.dto.request.editora.CreateEditoraRequest;
import com.GuilhermeGuerra.biblioteca.dto.request.editora.UpdateEditoraRequest;
import com.GuilhermeGuerra.biblioteca.dto.response.EditoraResponse;
import com.GuilhermeGuerra.biblioteca.entity.Editora;
import com.GuilhermeGuerra.biblioteca.exceptions.CategoriaNotFoundException;
import com.GuilhermeGuerra.biblioteca.exceptions.EditoraJaCadastradaException;
import com.GuilhermeGuerra.biblioteca.exceptions.EditoraNotFoundException;
import com.GuilhermeGuerra.biblioteca.repository.EditoraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EditoraService {

    private final EditoraRepository editoraRepository;

    @Transactional(readOnly = true)
    public Editora buscarReferenciaPorId(Long id) {
        if(!editoraRepository.existsById(id)) {
            throw new EditoraNotFoundException("Editora não encontrada");
        }
        return editoraRepository.getReferenceById(id);
    }

    @Transactional(readOnly = true)
    public List<EditoraResponse> findAll() {
        return editoraRepository.findAll().stream().map(EditoraResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public EditoraResponse findById(Long id) {
        Editora editora = editoraRepository.findById(id)
                .orElseThrow(() -> new EditoraNotFoundException("Editora não encontrada"));
        return EditoraResponse.fromEntity(editora);
    }


    @Transactional
    public EditoraResponse create(CreateEditoraRequest request) {
        if(editoraRepository.existsByNomeIgnoreCase(request.nome())) {
            throw new EditoraJaCadastradaException("Editora com nome " + request.nome() + " Já cadastrada");
        }

        Editora editora = Editora.builder().nome(request.nome())
                .pais(request.pais())
                .cidade(request.cidade())
                .estado(request.estado())
                .build();

        editoraRepository.save(editora);

        return EditoraResponse.fromEntity(editora);
    }

    @Transactional
    public EditoraResponse update(Long id, UpdateEditoraRequest request) {
        Editora editora = editoraRepository.findById(id)
                .orElseThrow(() -> new EditoraNotFoundException("Editora não encontrada"));

        if(request.nome() != null && !request.nome().isBlank()) {
            editora.setNome(request.nome());
        }

        if(request.cidade() != null && !request.cidade().isBlank()) {
            editora.setCidade(request.cidade());
        }

        if(request.pais() != null && !request.pais().isBlank()) {
            editora.setPais(request.pais());
        }

        if(request.estado() != null && !request.estado().isBlank()) {
            editora.setEstado(request.estado());
        }

        editoraRepository.save(editora);

        return EditoraResponse.fromEntity(editora);
    }

    @Transactional
    public void delete(Long id) {
        if(!editoraRepository.existsById(id)) {
            throw new CategoriaNotFoundException("Editora não encontrada");
        }
        editoraRepository.deleteById(id);
    }
}
