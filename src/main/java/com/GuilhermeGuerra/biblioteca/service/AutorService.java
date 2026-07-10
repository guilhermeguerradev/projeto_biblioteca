package com.GuilhermeGuerra.biblioteca.service;

import com.GuilhermeGuerra.biblioteca.dto.autor.CreateAutorRequest;
import com.GuilhermeGuerra.biblioteca.dto.autor.UpdateAutorRequest;
import com.GuilhermeGuerra.biblioteca.dto.autor.AutorResponse;
import com.GuilhermeGuerra.biblioteca.entity.Autor;
import com.GuilhermeGuerra.biblioteca.exceptions.BusinessException;
import com.GuilhermeGuerra.biblioteca.exceptions.NotFoundException;
import com.GuilhermeGuerra.biblioteca.repository.AutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AutorService {

    private final AutorRepository autorRepository;

    @Transactional(readOnly = true)
    public List<AutorResponse> findAll() {
        return autorRepository.findAll().stream().map(AutorResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public List<Autor> findAllById(List<Long> ids) {
        return autorRepository.findAllById(ids);
    }

    @Transactional(readOnly = true)
    public AutorResponse findById(Long id) {
        Autor autor = autorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Autor não encontrado"));
        return AutorResponse.fromEntity(autor);
    }

    @Transactional
    public AutorResponse create(CreateAutorRequest request) {
        Autor autor = Autor.builder()
                .nome(request.nome())
                .nacionalidade(request.nacionalidade())
                .build();

        autorRepository.save(autor);

        return AutorResponse.fromEntity(autor);
    }

    @Transactional
    public AutorResponse update(Long id,UpdateAutorRequest request) {

        Autor autor = autorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Autor não encontrado"));

        if(StringUtils.hasText(request.nome())) {
            autor.setNome(request.nome());
        }

        if(StringUtils.hasText(request.nacionalidade())) {
            autor.setNacionalidade(request.nacionalidade());
        }

        return AutorResponse.fromEntity(autor);
    }

    @Transactional
    public void delete(Long id) {
        if (!autorRepository.existsById(id)) {
            throw new NotFoundException("Autor não encontrado");
        }
        if(autorRepository.possuiLivrosVinculados(id)) {
            throw new BusinessException("Não é possível remover este autor pois ela está vinculada a um ou mais livros");
        }
        autorRepository.deleteById(id);
    }
}
