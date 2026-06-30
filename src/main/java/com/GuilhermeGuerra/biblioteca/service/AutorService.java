package com.GuilhermeGuerra.biblioteca.service;

import com.GuilhermeGuerra.biblioteca.dto.request.CreateAutorRequest;
import com.GuilhermeGuerra.biblioteca.dto.response.AutorResponse;
import com.GuilhermeGuerra.biblioteca.entity.Autor;
import com.GuilhermeGuerra.biblioteca.exceptions.AutorJaCadastradoException;
import com.GuilhermeGuerra.biblioteca.repository.AutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutorService {

    private final AutorRepository autorRepository;


    public AutorResponse create(CreateAutorRequest request) {
        Autor autor = autorRepository.findByNome(request.nome())
                .orElseThrow(() -> new AutorJaCadastradoException("Autor já cadastrado"));


    }
}
