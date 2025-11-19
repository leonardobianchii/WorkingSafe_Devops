package br.com.workingsafe.mapper;

import br.com.workingsafe.dto.TimeEquipeDto;
import br.com.workingsafe.model.Empresa;
import br.com.workingsafe.model.TimeEquipe;
import org.springframework.stereotype.Component;

@Component
public class TimeEquipeMapper {

    public TimeEquipe toEntity(TimeEquipeDto dto, Empresa empresa) {
        TimeEquipe time = new TimeEquipe();
        time.setId(dto.id());
        time.setEmpresa(empresa);
        time.setNome(dto.nome());
        time.setDescricao(dto.descricao());
        return time;
    }

    public TimeEquipeDto toDto(TimeEquipe time) {
        Long empresaId = time.getEmpresa() != null ? time.getEmpresa().getId() : null;

        return new TimeEquipeDto(
                time.getId(),
                empresaId,
                time.getNome(),
                time.getDescricao()
        );
    }
}
