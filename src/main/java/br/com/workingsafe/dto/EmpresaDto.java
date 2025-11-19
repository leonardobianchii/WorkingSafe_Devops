package br.com.workingsafe.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmpresaDto(

        Long id,

        @NotBlank(message = "Nome da empresa é obrigatório")
        String nome,

        String cnpj,

        @Email(message = "E-mail de contato inválido")
        String emailContato

) {}
