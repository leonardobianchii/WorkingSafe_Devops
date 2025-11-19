package br.com.workingsafe.dto;

import jakarta.validation.constraints.NotBlank;

public record PapelDto(

        Long id,

        @NotBlank(message = "Código do papel é obrigatório")
        String codigo,

        String descricao

) {}
