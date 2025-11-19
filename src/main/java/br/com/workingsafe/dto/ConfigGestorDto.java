package br.com.workingsafe.dto;

import jakarta.validation.constraints.*;

public record ConfigGestorDto(

        Long id,

        @NotNull(message = "Empresa é obrigatória")
        Long empresaId,

        Long timeId,

        @NotNull(message = "Limiar de alerta é obrigatório")
        @DecimalMin(value = "0.0", message = "Limiar mínimo é 0.0")
        @DecimalMax(value = "1.0", message = "Limiar máximo é 1.0")
        Double limiarAlerta,

        @NotNull(message = "Janela de dias é obrigatória")
        @Min(value = 1, message = "Janela mínima é 1 dia")
        @Max(value = 365, message = "Janela máxima é 365 dias")
        Integer janelaDias,

        // true = anonimizados, false = não anonimizados
        Boolean anonimizado

) {}
