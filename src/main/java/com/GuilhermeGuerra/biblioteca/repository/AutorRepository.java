package com.GuilhermeGuerra.biblioteca.repository;

import com.GuilhermeGuerra.biblioteca.entity.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {

    @Query("SELECT COUNT(l) > 0 " +
            "FROM Autor a " +
            "JOIN a.livros l " +
            "WHERE a.id = :autorId")
    boolean possuiLivrosVinculados(@Param("autorId") Long autorId);

}
