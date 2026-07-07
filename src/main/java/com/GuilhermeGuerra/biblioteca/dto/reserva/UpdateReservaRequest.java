package com.GuilhermeGuerra.biblioteca.dto.reserva;

import com.GuilhermeGuerra.biblioteca.entity.enums.StatusReserva;

public record UpdateReservaRequest(
        StatusReserva status
) {
}
