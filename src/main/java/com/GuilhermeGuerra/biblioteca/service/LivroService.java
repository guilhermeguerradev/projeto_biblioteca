package com.GuilhermeGuerra.biblioteca.service;

import com.GuilhermeGuerra.biblioteca.dto.livro.CreateLivroRequest;
import com.GuilhermeGuerra.biblioteca.dto.livro.UpdateLivroRequest;
import com.GuilhermeGuerra.biblioteca.dto.livro.LivroResponse;
import com.GuilhermeGuerra.biblioteca.entity.Autor;
import com.GuilhermeGuerra.biblioteca.entity.Categoria;
import com.GuilhermeGuerra.biblioteca.entity.Editora;
import com.GuilhermeGuerra.biblioteca.entity.Livro;
import com.GuilhermeGuerra.biblioteca.exceptions.*;
import com.GuilhermeGuerra.biblioteca.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository livroRepository;

    private final EditoraService editoraService;
    private final CategoriaService categoriaService;
    private final AutorService autorService;

    @Transactional(readOnly = true)
    public List<LivroResponse> findAll() {
        return  livroRepository.findAll().stream().map(LivroResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public LivroResponse findById(Long id) {
        Livro livro = findByIdOrElseThrow(id);
        return LivroResponse.fromEntity(livro);
    }

    @Transactional(readOnly = true)
    public Livro findByIdOrElseThrow(Long id) {
        return livroRepository.findById(id).orElseThrow(() -> new NotFoundException("Livro não encontrado"));
    }

    @Transactional
    public LivroResponse create(CreateLivroRequest request) {

        if (livroRepository.existsByIsbnIgnoreCase(request.isbn())) {
            throw new AlreadyRegisteredException("Já existe um livro cadastrado com o ISBN " + request.isbn());
        }

        Editora editora = editoraService.buscarReferenciaPorId(request.editoraId());

        List<Autor> autores = autorService.findAllById(request.autoresIds());
        List<Categoria> categorias = categoriaService.findAllById(request.categoriasIds());

        validarExistenciaDeCategorias(categorias, request.categoriasIds());
        validarExistenciaDeAutores(autores,request.autoresIds());

        Livro livro = Livro.builder()
                .titulo(request.titulo())
                .isbn(request.isbn())
                .anoPublicacao(request.anoPublicacao())
                .edicao(request.edicao())
                .idioma(request.idioma())
                .sinopse(request.sinopse())
                .numeroPaginas(request.numeroPaginas())
                .editora(editora)
                .categorias(categorias)
                .autores(autores)
                .build();

        livroRepository.save(livro);

        return LivroResponse.fromEntity(livro);

    }

    @Transactional
    public LivroResponse update(Long id,UpdateLivroRequest request) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Livro não encontrado"));

        if(StringUtils.hasText(request.titulo())) {
            livro.setTitulo(request.titulo());
        }
        if(StringUtils.hasText(request.isbn()) && !request.isbn().equalsIgnoreCase(livro.getIsbn())) {
            if(livroRepository.existsByIsbnIgnoreCase(request.isbn())) {
                throw new AlreadyRegisteredException("Já existe um livro cadastrado com o ISBN " + request.isbn());
            }
            livro.setIsbn(request.isbn());
        }
        if(StringUtils.hasText(request.edicao())) {
            livro.setEdicao(request.edicao());
        }
        if(StringUtils.hasText(request.idioma())) {
            livro.setIdioma(request.idioma());
        }
        if(StringUtils.hasText(request.sinopse())) {
            livro.setSinopse(request.sinopse());
        }
       if(request.anoPublicacao() != null) {
            livro.setAnoPublicacao(request.anoPublicacao());
       }
       if(request.numeroPaginas() != null) {
           livro.setNumeroPaginas(request.numeroPaginas());
       }

        if(request.editoraId() != null) {
            Editora editora = editoraService.buscarReferenciaPorId(request.editoraId());
            livro.setEditora(editora);
        }

        if(request.categoriasIds() != null && !request.categoriasIds().isEmpty()) {
            List<Categoria> novasCategorias = categoriaService.findAllById(request.categoriasIds());
            validarExistenciaDeCategorias(novasCategorias,request.categoriasIds());
            livro.getCategorias().clear();
            livro.getCategorias().addAll(novasCategorias);
        }

        if(request.autoresIds() != null && !request.autoresIds().isEmpty()) {
            List<Autor> novosAutores = autorService.findAllById(request.autoresIds());
            validarExistenciaDeAutores(novosAutores, request.autoresIds());
            livro.getAutores().clear();
            livro.getAutores().addAll(novosAutores);
        }

        return LivroResponse.fromEntity(livro);
    }

    @Transactional
    public void delete(Long id) {
        if (!livroRepository.existsById(id)) {
            throw new NotFoundException("Livro não encontrado");
        }

        if (livroRepository.possuiExemplaresVinculados(id)) {
            throw new BusinessException("Não é possível excluir o livro porque ele possui exemplares cadastrados.");
        }

        if (livroRepository.possuiReservasVinculadas(id)) {
            throw new BusinessException("Não é possível excluir o livro porque ele possui reservas cadastradas.");
        }

        livroRepository.deleteById(id);
    }




    private void validarExistenciaDeCategorias(List<Categoria> cadastradas, List<Long> requisitadas) {
        long idsUnicosRequisitados = requisitadas.stream().distinct().count();

        if(cadastradas.size() != idsUnicosRequisitados) {
            throw new NotFoundException("Uma ou mais categorias informadas não foram encontradas no sistema");
        }
    }

    private void validarExistenciaDeAutores(List<Autor> cadastrados, List<Long> requisitados) {
        long idsUnicosRequisitados = requisitados.stream().distinct().count();

        if(cadastrados.size() != idsUnicosRequisitados) {
            throw new NotFoundException("Um ou mais Autores informados não foram encontrados no sistema");
        }
    }


}
