package com.GuilhermeGuerra.biblioteca.service;

import com.GuilhermeGuerra.biblioteca.dto.exemplar.CreateExemplarRequest;
import com.GuilhermeGuerra.biblioteca.dto.exemplar.ExemplarResponse;
import com.GuilhermeGuerra.biblioteca.dto.exemplar.UpdateExemplarRequest;
import com.GuilhermeGuerra.biblioteca.entity.Exemplar;
import com.GuilhermeGuerra.biblioteca.entity.Livro;
import com.GuilhermeGuerra.biblioteca.entity.enums.StatusExemplar;
import com.GuilhermeGuerra.biblioteca.exceptions.BusinessException;
import com.GuilhermeGuerra.biblioteca.exceptions.ExemplarNotFoundException;
import com.GuilhermeGuerra.biblioteca.repository.ExemplarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExemplarService {

    private final ExemplarRepository repository;

    private final LivroService livroService;

    @Transactional(readOnly = true)
    public Exemplar findByIdOrElseThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new ExemplarNotFoundException("Exemplar não encontrado"));
    }

    @Transactional(readOnly = true)
    public ExemplarResponse findById(Long id) {
        Exemplar exemplar = findByIdOrElseThrow(id);
        return ExemplarResponse.fromEntity(exemplar);
    }

    @Transactional(readOnly = true)
    public List<ExemplarResponse> findAll() {
        return repository.findAll().stream().map(ExemplarResponse::fromEntity).toList();
    }

    @Transactional
    public ExemplarResponse create(CreateExemplarRequest request) {
        Livro livro = livroService.findByIdOrElseThrow(request.livroId());

        String codigoPatrimonio = UUID.randomUUID().toString();

        LocalDate dataAquisicao;

        if(request.dataAquisicao() == null) {
            dataAquisicao = LocalDate.now();
        } else {
            dataAquisicao = request.dataAquisicao();
        }

        Exemplar exemplar = Exemplar.builder()
                .livro(livro)
                .codigoPatrimonio(codigoPatrimonio)
                .status(request.status())
                .localizacao(request.localizacao())
                .dataAquisicao(dataAquisicao)
                .observacao(request.observacao())
                .build();

        repository.save(exemplar);

        return ExemplarResponse.fromEntity(exemplar);
    }

    @Transactional
    public ExemplarResponse update(Long id, UpdateExemplarRequest request) {

        Exemplar exemplar = findByIdOrElseThrow(id);

        if( request.livroId() != null && request.livroId() > 0 && !(request.livroId().equals(exemplar.getLivro().getId())) ) {
            Livro livro = livroService.findByIdOrElseThrow(request.livroId());
            exemplar.setLivro(livro);
        }

        if(request.status() != null && request.status() != exemplar.getStatus()) {
            exemplar.setStatus(request.status());
        }

        if(StringUtils.hasText(request.localizacao())) {
            exemplar.setLocalizacao(request.localizacao());
        }

        if(StringUtils.hasText(request.observacao())) {
            exemplar.setObservacao(request.observacao());
        }

        if(request.dataAquisicao() != null) {
            exemplar.setDataAquisicao(request.dataAquisicao());
        }

        return ExemplarResponse.fromEntity(exemplar);
    }

    @Transactional
    public void delete(Long id) {
        Exemplar exemplar = findByIdOrElseThrow(id);

        if (exemplar.getStatus() == StatusExemplar.RESERVADO ||
            exemplar.getStatus() == StatusExemplar.EMPRESTADO) {
            throw new BusinessException("Exemplar não pode ser apagado pois está reservado ou emprestado");
        }
        repository.delete(exemplar);
    }


}
