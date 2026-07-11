package com.GuilhermeGuerra.biblioteca.dto.emprestimo;


import com.GuilhermeGuerra.biblioteca.entity.enums.StatusEmprestimo;

public record UpdateEmprestimoRequest(
        StatusEmprestimo status
) {
}
