package com.GuilhermeGuerra.biblioteca.dto.multa;


import com.GuilhermeGuerra.biblioteca.entity.enums.StatusExemplar;

public record CreateMultaRequest(
        Long emprestimoId,
        String motivo,
        StatusExemplar statusExemplar
) {
}
