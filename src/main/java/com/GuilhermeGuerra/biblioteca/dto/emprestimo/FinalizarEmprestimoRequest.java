package com.GuilhermeGuerra.biblioteca.dto.emprestimo;

import com.GuilhermeGuerra.biblioteca.entity.enums.StatusExemplar;

public record FinalizarEmprestimoRequest(
        StatusExemplar statusExemplar;
) {
}
