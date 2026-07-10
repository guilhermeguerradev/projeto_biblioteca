package com.GuilhermeGuerra.biblioteca.service;

import com.GuilhermeGuerra.biblioteca.dto.categoria.CreateCategoriaRequest;
import com.GuilhermeGuerra.biblioteca.dto.categoria.UpdateCategoriaRequest;
import com.GuilhermeGuerra.biblioteca.dto.categoria.CategoriaResponse;
import com.GuilhermeGuerra.biblioteca.entity.Categoria;
import com.GuilhermeGuerra.biblioteca.exceptions.AlreadyRegisteredException;
import com.GuilhermeGuerra.biblioteca.exceptions.BusinessException;
import com.GuilhermeGuerra.biblioteca.exceptions.NotFoundException;
import com.GuilhermeGuerra.biblioteca.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
    public List<Categoria> findAllById(List<Long> ids) {
        return categoriaRepository.findAllById(ids);
    }

    @Transactional(readOnly = true)
    public CategoriaResponse findById(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada"));

        return CategoriaResponse.fromEntity(categoria);
    }

    @Transactional
    public CategoriaResponse create(CreateCategoriaRequest request) {
        if(categoriaRepository.existsByNomeIgnoreCase(request.nome())) {
            throw new AlreadyRegisteredException("Categoria já cadastrada");
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
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada"));

        if(StringUtils.hasText(request.nome()) && !request.nome().equalsIgnoreCase(categoria.getNome())) {
            if(categoriaRepository.existsByNomeIgnoreCase(request.nome())) {
                throw new AlreadyRegisteredException("Categoria com esse nome já existente");
            }
            categoria.setNome(request.nome());
        }
        if(StringUtils.hasText(request.descricao())) {
            categoria.setDescricao(request.descricao());
        }

        return CategoriaResponse.fromEntity(categoria);
    }

    @Transactional
    public void delete(Long id) {
        if(!categoriaRepository.existsById(id)) {
            throw new NotFoundException("Categoria não encontrada");
        }
        if(this.categoriaRepository.possuiuLivrosVinculados(id)) {
            throw new BusinessException("Não é possível remover esta categoria pois ela está vinculada a um ou mais livros");
        }
        this.categoriaRepository.deleteById(id);
    }
}
