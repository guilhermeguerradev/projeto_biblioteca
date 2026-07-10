package com.GuilhermeGuerra.biblioteca.dto.emprestimo;

import com.GuilhermeGuerra.biblioteca.entity.enums.StatusExemplar;
import jakarta.validation.constraints.NotNull;

public record FinalizarEmprestimoRequest(
        @NotNull(message = "Obrigatório informar a condição do exemplar")
        StatusExemplar statusExemplar
) {
}
