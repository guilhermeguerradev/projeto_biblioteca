package com.GuilhermeGuerra.biblioteca.repository;

import com.GuilhermeGuerra.biblioteca.entity.Emprestimo;
import com.GuilhermeGuerra.biblioteca.entity.enums.StatusEmprestimo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmprestimoRepository extends JpaRepository<Emprestimo,Long> {

    int countByUsuarioIdAndStatus(Long usuarioId, StatusEmprestimo status);
}
