package com.GuilhermeGuerra.biblioteca.dto;

import java.time.Instant;

public record ErroResponse(String mensagem, Instant timestamp, int status) {

    public ErroResponse(String mensagem, int status) {
        this(mensagem, Instant.now(), status);
    }
}
