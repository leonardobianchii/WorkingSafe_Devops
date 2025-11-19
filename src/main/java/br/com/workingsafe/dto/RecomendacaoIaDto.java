package br.com.workingsafe.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record RecomendacaoIaDto(
        Long id,
        Long usuarioId,
        String categoria,
        String descricao,
        LocalDateTime dataCriacao,
        LocalDate dataValidade
) {}
