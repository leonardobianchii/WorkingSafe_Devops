package br.com.workingsafe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TimeEquipeDto(

        Long id,

        @NotNull(message = "Empresa é obrigatória")
        Long empresaId,

        @NotBlank(message = "Nome do time é obrigatório")
        String nome,

        String descricao

) {}
