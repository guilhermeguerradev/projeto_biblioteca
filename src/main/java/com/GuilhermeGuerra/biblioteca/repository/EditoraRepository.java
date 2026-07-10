package com.GuilhermeGuerra.biblioteca.repository;

import com.GuilhermeGuerra.biblioteca.entity.Editora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EditoraRepository extends JpaRepository<Editora,Long> {

    boolean existsByNomeIgnoreCase(String nome);

    @Query("SELECT COUNT(l) > 0 " +
            "FROM Editora e " +
            "JOIN e.livros l " +
            "WHERE e.id = :editoraId")
    boolean possuiuLivrosVinculados(@Param("editoraId") Long categoriaId);
}
