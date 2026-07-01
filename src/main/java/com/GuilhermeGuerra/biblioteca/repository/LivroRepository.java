package com.GuilhermeGuerra.biblioteca.repository;

import com.GuilhermeGuerra.biblioteca.entity.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LivroRepository extends JpaRepository<Livro,Long> {

    boolean existsByTituloIgnoreCase(String titulo);
}
