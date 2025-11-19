package br.com.workingsafe.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioDto(
        Long id,

        @NotNull(message = "Empresa é obrigatória")
        Long empresaId,

        Long timeId,

        @NotBlank(message = "Nome não pode ser vazio")
        String nome,

        @Email(message = "E-mail inválido")
        @NotBlank(message = "E-mail é obrigatório")
        String email,

        String fusoHorario,

        Boolean ativo,


        String empresaNome,
        String timeNome
) {}
