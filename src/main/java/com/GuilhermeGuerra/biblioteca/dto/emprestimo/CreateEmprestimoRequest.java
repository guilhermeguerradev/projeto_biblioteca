package com.GuilhermeGuerra.biblioteca.dto.emprestimo;

import com.GuilhermeGuerra.biblioteca.entity.enums.StatusEmprestimo;
import jakarta.validation.constraints.NotNull;

public record CreateEmprestimoRequest(

        @NotNull(message = "O ID do usuário é obrigatório")
        Long usuarioId,

        @NotNull(message = "O ID do colaborador é obrigatório")
        Long colaboradorId,

        @NotNull(message = "O ID do exemplar é obrigatório")
        Long livroId

) {
}
