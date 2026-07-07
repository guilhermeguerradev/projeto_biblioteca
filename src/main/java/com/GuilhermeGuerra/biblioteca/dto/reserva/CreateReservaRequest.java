package com.GuilhermeGuerra.biblioteca.dto.reserva;

import com.GuilhermeGuerra.biblioteca.entity.enums.StatusReserva;
import jakarta.validation.constraints.NotNull;


public record CreateReservaRequest (
        @NotNull(message = "Id do usuario deve ser informado")
        Long usuarioId,
        @NotNull(message = "Id do livro deve ser inforadmo")
        Long livroId,

        StatusReserva status
){
}
