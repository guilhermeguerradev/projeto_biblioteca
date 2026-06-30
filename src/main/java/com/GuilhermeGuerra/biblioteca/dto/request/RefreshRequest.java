package com.GuilhermeGuerra.biblioteca.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
        @NotBlank(message = "refresh token obrigatório")
        String refreshToken
) {
}
