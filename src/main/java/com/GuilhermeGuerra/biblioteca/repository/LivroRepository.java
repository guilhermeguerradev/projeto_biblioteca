package com.GuilhermeGuerra.biblioteca.repository;

import com.GuilhermeGuerra.biblioteca.entity.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LivroRepository extends JpaRepository<Livro,Long> {

    boolean existsByIsbnIgnoreCase(String isbn);

    @Query("SELECT COUNT(e) > 0 " +
            "FROM Exemplar e " +
            "WHERE e.livro.id = :livroId")
    boolean possuiExemplaresVinculados(@Param("livroId") Long livroId);

    @Query("SELECT COUNT(r) > 0 " +
            "FROM Reserva r " +
            "WHERE r.livro.id = :livroId")
    boolean possuiReservasVinculadas(@Param("livroId") Long livroId);

    @Query("SELECT COUNT(e) > 0 " +
            "FROM Exemplar e " +
            "WHERE e.livro.id = :livroId AND " +
            "e.status = 'DISPONIVEL'")
    boolean possuiExemplarDisponivel(@Param("livroId") Long livroId);

    @Query("SELECT COUNT(e) > 0 " +
            "FROM Exemplar e " +
            "WHERE e.livro.id = :livroId AND " +
            "e.status IN ('EMPRESTADO', 'RESERVADO')")
    boolean possuiExemplaresCirculando(@Param("livroId") Long livroId);



}
