package com.GuilhermeGuerra.biblioteca.service;

import com.GuilhermeGuerra.biblioteca.dto.request.livro.CreateLivroRequest;
import com.GuilhermeGuerra.biblioteca.dto.request.livro.UpdateLivroRequest;
import com.GuilhermeGuerra.biblioteca.dto.response.LivroResponse;
import com.GuilhermeGuerra.biblioteca.entity.Autor;
import com.GuilhermeGuerra.biblioteca.entity.Categoria;
import com.GuilhermeGuerra.biblioteca.entity.Editora;
import com.GuilhermeGuerra.biblioteca.entity.Livro;
import com.GuilhermeGuerra.biblioteca.exceptions.*;
import com.GuilhermeGuerra.biblioteca.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return livroRepository.findById(id).orElseThrow(() -> new LivroNotFoundException("Livro não encontrado"));
    }

    @Transactional
    public LivroResponse create(CreateLivroRequest request) {

        if(livroRepository.existsByTituloIgnoreCase(request.titulo())) {
            throw new LivroJaCadastradoException(request.titulo());
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
                .orElseThrow(() -> new LivroNotFoundException("Livro não encontrado"));

        if(request.titulo() != null && !request.titulo().isBlank()) {
            livro.setTitulo(request.titulo());
        }
        if(request.isbn() != null && !request.isbn().isBlank()) {
            livro.setIsbn(request.isbn());
        }
        if(request.edicao() != null && !request.edicao().isBlank()) {
            livro.setEdicao(request.edicao());
        }
        if(request.idioma() != null && !request.idioma().isBlank()) {
            livro.setIdioma(request.idioma());
        }
        if(request.sinopse() != null && !request.sinopse().isBlank()) {
            livro.setSinopse(request.sinopse());
        }
       if(request.anoPublicacao() > 0) {
            livro.setAnoPublicacao(request.anoPublicacao());
       }
       if(request.numeroPaginas() > 0) {
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

        livroRepository.save(livro);

        return LivroResponse.fromEntity(livro);
    }

    @Transactional
    public void delete(Long id) {
        Livro livro = livroRepository.findById(id).orElseThrow(() -> new LivroNotFoundException("Livro não encontrado"));

        if(!livro.getExemplares().isEmpty()) {
            throw new BusinessException("Não é possível excluir o livro porque ele possui exemplares cadastrados.");
        }
        if(!livro.getReservas().isEmpty()) {
            throw new BusinessException("Não é possível excluir o livro porque ele possui reservas cadastradas.");
        }

        livroRepository.delete(livro);
    }




    private void validarExistenciaDeCategorias(List<Categoria> cadastradas, List<Long> requisitadas) {
        long idsUnicosRequisitados = requisitadas.stream().distinct().count();

        if(cadastradas.size() != idsUnicosRequisitados) {
            throw new CategoriaNotFoundException("Uma ou mais categorias informadas não foram encontradas no sistema");
        }
    }

    public void validarExistenciaDeAutores(List<Autor> cadastrados, List<Long> requisitados) {
        long idsUnicosRequisistados = requisitados.stream().distinct().count();

        if(cadastrados.size() != idsUnicosRequisistados) {
            throw new AutorNotFoundException("Um ou mais Autores informados não foram encontrados no sistema");
        }
    }


}
