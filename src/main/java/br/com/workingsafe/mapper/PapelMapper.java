package br.com.workingsafe.mapper;

import br.com.workingsafe.dto.PapelDto;
import br.com.workingsafe.model.Papel;
import org.springframework.stereotype.Component;

@Component
public class PapelMapper {

    public Papel toEntity(PapelDto dto) {
        Papel papel = new Papel();
        papel.setId(dto.id());
        papel.setCodigo(dto.codigo());
        papel.setDescricao(dto.descricao());
        return papel;
    }

    public PapelDto toDto(Papel papel) {
        return new PapelDto(
                papel.getId(),
                papel.getCodigo(),
                papel.getDescricao()
        );
    }
}
