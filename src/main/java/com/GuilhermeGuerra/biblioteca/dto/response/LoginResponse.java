package com.GuilhermeGuerra.biblioteca.dto.response;

public record LoginResponse(
        String accessToken,
        String refrehToken
) { }
