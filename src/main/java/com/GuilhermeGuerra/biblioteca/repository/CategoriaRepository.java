package com.GuilhermeGuerra.biblioteca.repository;

import com.GuilhermeGuerra.biblioteca.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoriaRepository extends JpaRepository<Categoria,Long> {

    boolean existsByNomeIgnoreCase(String nome);

    @Query("SELECT COUNT(l) > 0 " +
            "FROM Categoria c " +
            "JOIN c.livros l " +
            "WHERE c.id = :categoriaId")
    boolean possuiuLivrosVinculados(@Param("categoriaId") Long categoriaId);
}
