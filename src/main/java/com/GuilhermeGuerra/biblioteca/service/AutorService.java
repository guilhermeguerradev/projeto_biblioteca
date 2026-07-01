package com.GuilhermeGuerra.biblioteca.service;

import com.GuilhermeGuerra.biblioteca.dto.request.autor.CreateAutorRequest;
import com.GuilhermeGuerra.biblioteca.dto.request.autor.UpdateAutorRequest;
import com.GuilhermeGuerra.biblioteca.dto.response.AutorResponse;
import com.GuilhermeGuerra.biblioteca.entity.Autor;
import com.GuilhermeGuerra.biblioteca.exceptions.AutorJaCadastradoException;
import com.GuilhermeGuerra.biblioteca.exceptions.AutorNotFoundException;
import com.GuilhermeGuerra.biblioteca.repository.AutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AutorService {

    private final AutorRepository autorRepository;

    @Transactional(readOnly = true)
    public List<AutorResponse> findAll() {
        List<AutorResponse> autores = autorRepository.findAll()
                .stream()
                .map(autor -> AutorResponse.fromEntity(autor))
                .toList();
        return autores;
    }
    @Transactional(readOnly = true)
    public AutorResponse findById(Long id) {
        Autor autor = autorRepository.findById(id)
                .orElseThrow(() -> new AutorNotFoundException("Autor não encontrado"));

        return AutorResponse.fromEntity(autor);
    }

    @Transactional
    public AutorResponse create(CreateAutorRequest request) {

        if(autorRepository.existsByNome(request.nome())) {
            new AutorJaCadastradoException("Autor já cadastrado exception");
        }

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
                .orElseThrow(() -> new AutorNotFoundException("Autor não encontrado"));

        if(!request.nome().isBlank() && request.nome() != null) {
            autor.setNome(request.nome());
        }

        if(!request.nacionalidade().isBlank() && request.nome() != null) {
            autor.setNacionalidade(request.nacionalidade());
        }

        autorRepository.save(autor);

        return AutorResponse.fromEntity(autor);
    }

    @Transactional
    public void delete(Long id) {
        if (!autorRepository.existsById(id)) {
            throw new AutorNotFoundException("Autor não encontrado");
        }
        autorRepository.deleteById(id);
    }
}
