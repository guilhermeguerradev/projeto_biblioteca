package com.GuilhermeGuerra.biblioteca.dto.exemplar;

import com.GuilhermeGuerra.biblioteca.entity.enums.StatusExemplar;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateExemplarRequest(
        @NotNull(message = "Exemplar deve estar vinculado a algum livro.")
        Long livroId,

        @NotNull(message = "Status do exemplar deve ser informado.")
        StatusExemplar status,

        @NotBlank(message = "Deve ser informado a localização do exemplar")
        String localizacao,

        LocalDate dataAquisicao,

        String observacao

) {
}
