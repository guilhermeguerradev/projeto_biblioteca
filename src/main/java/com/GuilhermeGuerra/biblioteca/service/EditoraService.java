package com.GuilhermeGuerra.biblioteca.service;

import com.GuilhermeGuerra.biblioteca.dto.editora.CreateEditoraRequest;
import com.GuilhermeGuerra.biblioteca.dto.editora.UpdateEditoraRequest;
import com.GuilhermeGuerra.biblioteca.dto.editora.EditoraResponse;
import com.GuilhermeGuerra.biblioteca.entity.Editora;
import com.GuilhermeGuerra.biblioteca.exceptions.*;
import com.GuilhermeGuerra.biblioteca.repository.EditoraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EditoraService {

    private final EditoraRepository editoraRepository;

    @Transactional(readOnly = true)
    public Editora buscarReferenciaPorId(Long id) {
        if(!editoraRepository.existsById(id)) {
            throw new NotFoundException("Editora não encontrada");
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
                .orElseThrow(() -> new NotFoundException("Editora não encontrada"));
        return EditoraResponse.fromEntity(editora);
    }


    @Transactional
    public EditoraResponse create(CreateEditoraRequest request) {

        if(editoraRepository.existsByNomeIgnoreCase(request.nome())) {
            throw new AlreadyRegisteredException("Editora com nome " + request.nome() + " Já cadastrada");
        }

        Editora editora = Editora.builder()
                .nome(request.nome())
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
                .orElseThrow(() -> new NotFoundException("Editora não encontrada"));

        if(StringUtils.hasText(request.nome()) && !request.nome().equalsIgnoreCase(editora.getNome())) {
            if(editoraRepository.existsByNomeIgnoreCase(request.nome())){
                throw new AlreadyRegisteredException("Editora com nome " + request.nome() + " Já cadastrada");
            }
            editora.setNome(request.nome());
        }

        if(StringUtils.hasText(request.cidade())) {
            editora.setCidade(request.cidade());
        }

        if(StringUtils.hasText(request.pais())) {
            editora.setPais(request.pais());
        }

        if(StringUtils.hasText(request.estado())) {
            editora.setEstado(request.estado());
        }

        return EditoraResponse.fromEntity(editora);
    }

    @Transactional
    public void delete(Long id) {
        if(!editoraRepository.existsById(id)) {
            throw new NotFoundException("Editora não encontrada");
        }
        if(editoraRepository.possuiuLivrosVinculados(id)) {
            throw new BusinessException("Não é possível remover esta editora pois ela está vinculada a um ou mais livros");
        }
        editoraRepository.deleteById(id);
    }
}
