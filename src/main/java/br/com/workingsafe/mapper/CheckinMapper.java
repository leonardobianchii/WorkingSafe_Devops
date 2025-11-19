package br.com.workingsafe.mapper;

import br.com.workingsafe.dto.CheckinDto;
import br.com.workingsafe.model.Checkin;
import br.com.workingsafe.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class CheckinMapper {

    public Checkin toEntity(CheckinDto dto, Usuario usuario) {
        Checkin checkin = new Checkin();
        checkin.setId(dto.id());
        checkin.setUsuario(usuario);
        checkin.setDataHora(dto.dataHora());
        checkin.setHumor(dto.humor());
        checkin.setFoco(dto.foco());
        checkin.setMinutosPausas(dto.minutosPausas());
        checkin.setHorasTrabalhadas(dto.horasTrabalhadas());
        checkin.setObservacoes(dto.observacoes());
        checkin.setTags(dto.tags());

        String origem = dto.origem();
        if (origem == null || origem.isBlank()) {
            origem = "MOBILE";
        }
        checkin.setOrigem(origem);

        return checkin;
    }

    public CheckinDto toDto(Checkin checkin) {
        Long usuarioId = checkin.getUsuario() != null ? checkin.getUsuario().getId() : null;

        return new CheckinDto(
                checkin.getId(),
                usuarioId,
                checkin.getDataHora(),
                checkin.getHumor(),
                checkin.getFoco(),
                checkin.getMinutosPausas(),
                checkin.getHorasTrabalhadas(),
                checkin.getObservacoes(),
                checkin.getTags(),
                checkin.getOrigem()
        );
    }
}
