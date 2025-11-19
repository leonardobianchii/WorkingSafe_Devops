package br.com.workingsafe.mapper;

import br.com.workingsafe.dto.RecomendacaoIaDto;
import br.com.workingsafe.model.RecomendacaoIa;
import br.com.workingsafe.model.Usuario;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class RecomendacaoIaMapper {

    public RecomendacaoIa toEntity(RecomendacaoIaDto dto, Usuario usuario) {
        RecomendacaoIa rec = new RecomendacaoIa();
        rec.setId(dto.id());
        rec.setUsuario(usuario);
        rec.setCategoria(dto.categoria());
        rec.setDescricao(dto.descricao());
        rec.setDataCriacao(dto.dataCriacao());
        rec.setDataValidade(dto.dataValidade());
        rec.setOrigem("REGRAS_LOCAL");
        return rec;
    }

    public RecomendacaoIaDto toDto(RecomendacaoIa rec) {
        return new RecomendacaoIaDto(
                rec.getId(),
                rec.getUsuario().getId(),
                rec.getCategoria(),
                rec.getDescricao(),
                rec.getDataCriacao(),
                rec.getDataValidade()
        );
    }
}
