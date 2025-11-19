package br.com.workingsafe.mapper;

import br.com.workingsafe.dto.AggIndiceSemanalDto;
import br.com.workingsafe.model.AggIndiceSemanal;
import br.com.workingsafe.model.Empresa;
import br.com.workingsafe.model.TimeEquipe;
import org.springframework.stereotype.Component;

@Component
public class AggIndiceSemanalMapper {

    public AggIndiceSemanal toEntity(AggIndiceSemanalDto dto, Empresa empresa, TimeEquipe time) {
        AggIndiceSemanal agg = new AggIndiceSemanal();
        agg.setId(dto.id());
        agg.setEmpresa(empresa);
        agg.setTime(time);
        agg.setInicioSemana(dto.inicioSemana());
        agg.setQtdUsuarios(dto.qtdUsuarios());
        agg.setMediaIndice(dto.mediaIndice());
        agg.setMediaRisco(dto.mediaRisco());
        agg.setDtGeracao(dto.dtGeracao());
        return agg;
    }

    public AggIndiceSemanalDto toDto(AggIndiceSemanal agg) {
        Long empresaId = agg.getEmpresa() != null ? agg.getEmpresa().getId() : null;
        Long timeId = agg.getTime() != null ? agg.getTime().getId() : null;

        return new AggIndiceSemanalDto(
                agg.getId(),
                empresaId,
                timeId,
                agg.getInicioSemana(),
                agg.getQtdUsuarios(),
                agg.getMediaIndice(),
                agg.getMediaRisco(),
                agg.getDtGeracao()
        );
    }
}
