package com.GuilhermeGuerra.biblioteca.repository;

import com.GuilhermeGuerra.biblioteca.entity.Emprestimo;
import com.GuilhermeGuerra.biblioteca.entity.enums.StatusEmprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EmprestimoRepository extends JpaRepository<Emprestimo,Long> {

    int countByUsuarioIdAndStatus(Long usuarioId, StatusEmprestimo status);

    boolean existsByUsuarioIdAndStatus(Long usuarioId, StatusEmprestimo status);

    List<Emprestimo> findAllByStatusAndDataPrevistaBefore(StatusEmprestimo status, LocalDateTime limite);

    @Query("SELECT COUNT(emp) > 0 " +
            "FROM Emprestimo emp " +
            "WHERE emp.usuario.id = :usuarioId AND " +
            "emp.multa IS NOT NULL AND " +
            "emp.multa.status = 'PENDENTE'")
    boolean possuiMultasPendentes(@Param("usuarioId") Long usuarioId);

    @Query("SELECT COUNT(emp) > 0 " +
            "FROM Emprestimo emp " +
            "WHERE " +
            "emp.usuario.id = :usuarioId AND " +
            "emp.exemplar.livro.id = :livroId AND " +
            "emp.status = 'ATIVO'")
    boolean usuarioJaPossuiOLivroEmprestado(@Param("usuarioId") Long usuarioId, @Param("livroId") Long livroId);
}
