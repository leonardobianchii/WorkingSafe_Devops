package br.com.workingsafe.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record CheckinDto(

        Long id,

        @NotNull(message = "Usuário é obrigatório")
        Long usuarioId,

        @NotNull(message = "Data e hora do check-in são obrigatórias")
        LocalDateTime dataHora,

        @NotNull(message = "Humor é obrigatório")
        @Min(value = 1, message = "Humor deve ser no mínimo 1")
        @Max(value = 5, message = "Humor deve ser no máximo 5")
        Integer humor,

        @NotNull(message = "Foco é obrigatório")
        @Min(value = 1, message = "Foco deve ser no mínimo 1")
        @Max(value = 5, message = "Foco deve ser no máximo 5")
        Integer foco,

        @Min(value = 0, message = "Minutos de pausa não podem ser negativos")
        @Max(value = 480, message = "Minutos de pausa não podem passar de 480")
        Integer minutosPausas,

        @DecimalMin(value = "0.0", message = "Horas trabalhadas não podem ser negativas")
        @DecimalMax(value = "24.0", message = "Horas trabalhadas não podem passar de 24")
        Double horasTrabalhadas,

        String observacoes,

        String tags,

        String origem

) {}
