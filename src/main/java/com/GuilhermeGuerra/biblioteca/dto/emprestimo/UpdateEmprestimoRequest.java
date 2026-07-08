package com.GuilhermeGuerra.biblioteca.dto.emprestimo;


import com.GuilhermeGuerra.biblioteca.entity.enums.StatusEmprestimo;

import java.time.LocalDateTime;

public record UpdateEmprestimoRequest(
        StatusEmprestimo status,
        LocalDateTime dataDevolucao
) {
}
