package com.GuilhermeGuerra.biblioteca.service;

import com.GuilhermeGuerra.biblioteca.dto.request.livro.CreateLivroRequest;
import com.GuilhermeGuerra.biblioteca.dto.response.LivroResponse;
import com.GuilhermeGuerra.biblioteca.entity.Autor;
import com.GuilhermeGuerra.biblioteca.entity.Categoria;
import com.GuilhermeGuerra.biblioteca.entity.Editora;
import com.GuilhermeGuerra.biblioteca.entity.Livro;
import com.GuilhermeGuerra.biblioteca.exceptions.AutorNotFoundException;
import com.GuilhermeGuerra.biblioteca.exceptions.CategoriaNotFoundException;
import com.GuilhermeGuerra.biblioteca.exceptions.EditoraNotFoundException;
import com.GuilhermeGuerra.biblioteca.exceptions.LivroJaCadastradoException;
import com.GuilhermeGuerra.biblioteca.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository livroRepository;

    private final EditoraService editoraService;
    private final CategoriaService categoriaService;
    private final AutorService autorService;



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
