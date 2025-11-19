package br.com.workingsafe.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AggIndiceSemanalDto(

        Long id,

        @NotNull(message = "Empresa é obrigatória")
        Long empresaId,

        Long timeId,

        @NotNull(message = "Início da semana é obrigatório")
        LocalDate inicioSemana,

        @NotNull(message = "Quantidade de usuários é obrigatória")
        @Min(value = 1, message = "Quantidade mínima é 1")
        Integer qtdUsuarios,

        @NotNull(message = "Média de índice é obrigatória")
        @DecimalMin(value = "0.0", message = "Índice mínimo é 0.0")
        @DecimalMax(value = "1.0", message = "Índice máximo é 1.0")
        Double mediaIndice,

        @NotNull(message = "Média de risco é obrigatória")
        @DecimalMin(value = "0.0", message = "Risco mínimo é 0.0")
        @DecimalMax(value = "1.0", message = "Risco máximo é 1.0")
        Double mediaRisco,

        LocalDateTime dtGeracao

) {}
