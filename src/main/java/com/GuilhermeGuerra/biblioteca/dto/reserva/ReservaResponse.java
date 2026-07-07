package com.GuilhermeGuerra.biblioteca.dto.reserva;


import com.GuilhermeGuerra.biblioteca.entity.Reserva;
import com.GuilhermeGuerra.biblioteca.entity.enums.StatusReserva;

import java.time.LocalDateTime;

public record ReservaResponse(
    Long id,
    Long idUsuario,
    String nomeUsuario,
    Long idTitulo,
    String tituloLivro,
    LocalDateTime dataReserva,
    StatusReserva status
) {
    public static ReservaResponse fromEntity(Reserva reserva) {
        return new ReservaResponse(
                reserva.getId(),
                reserva.getUsuario().getId(),
                reserva.getUsuario().getNome(),
                reserva.getLivro().getId(),
                reserva.getLivro().getTitulo(),
                reserva.getDataReserva(),
                reserva.getStatus()
        );
    }
}
