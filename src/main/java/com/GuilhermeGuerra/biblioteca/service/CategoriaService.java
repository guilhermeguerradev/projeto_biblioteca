package com.GuilhermeGuerra.biblioteca.service;

import com.GuilhermeGuerra.biblioteca.dto.request.categoria.CreateCategoriaRequest;
import com.GuilhermeGuerra.biblioteca.dto.request.categoria.UpdateCategoriaRequest;
import com.GuilhermeGuerra.biblioteca.dto.response.CategoriaResponse;
import com.GuilhermeGuerra.biblioteca.entity.Categoria;
import com.GuilhermeGuerra.biblioteca.exceptions.CategoriaJaCadastradaException;
import com.GuilhermeGuerra.biblioteca.exceptions.CategoriaNotFoundException;
import com.GuilhermeGuerra.biblioteca.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<CategoriaResponse> findAll()  {
        return categoriaRepository.findAll().stream()
                .map(CategoriaResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse findById(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNotFoundException("Categoria não encontrada"));

        return CategoriaResponse.fromEntity(categoria);
    }



    @Transactional
    public CategoriaResponse create(CreateCategoriaRequest request) {

        if(categoriaRepository.existsByNomeIgnoreCase(request.nome())) {
            throw new CategoriaJaCadastradaException("Categoria já cadastrada");
        }

        Categoria categoria = Categoria.builder()
                .nome(request.nome())
                .descricao(request.descricao())
                .build();

        categoriaRepository.save(categoria);

        return CategoriaResponse.fromEntity(categoria);
    }


    @Transactional
    public CategoriaResponse update(Long id, UpdateCategoriaRequest request) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNotFoundException("Categoria não encontrada"));

        if(request.nome() != null && !request.nome().isBlank()) {
            categoria.setNome(request.nome());
        }
        if(request.descricao() != null && !request.descricao().isBlank()) {
            categoria.setDescricao(request.descricao());
        }

        categoriaRepository.save(categoria);

        return CategoriaResponse.fromEntity(categoria);
    }

    @Transactional
    public void delete(Long id) {

        if(!categoriaRepository.existsById(id)) {
            throw new CategoriaNotFoundException("Categoria não encontrada");
        }
        categoriaRepository.deleteById(id);
    }
}
