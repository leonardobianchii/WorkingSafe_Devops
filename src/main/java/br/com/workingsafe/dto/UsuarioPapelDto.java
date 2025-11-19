package br.com.workingsafe.dto;

import jakarta.validation.constraints.NotNull;

public record UsuarioPapelDto(

        Long id,

        @NotNull(message = "Usuário é obrigatório")
        Long usuarioId,

        @NotNull(message = "Papel é obrigatório")
        Long papelId

) {}
