package br.com.workingsafe.mapper;

import br.com.workingsafe.dto.ConfigGestorDto;
import br.com.workingsafe.model.ConfigGestor;
import br.com.workingsafe.model.Empresa;
import br.com.workingsafe.model.TimeEquipe;
import org.springframework.stereotype.Component;

@Component
public class ConfigGestorMapper {

    public ConfigGestor toEntity(ConfigGestorDto dto, Empresa empresa, TimeEquipe time) {
        ConfigGestor config = new ConfigGestor();
        config.setId(dto.id());
        config.setEmpresa(empresa);
        config.setTime(time);
        config.setLimiarAlerta(dto.limiarAlerta());
        config.setJanelaDias(dto.janelaDias());

        String flag = (dto.anonimizado() == null || dto.anonimizado()) ? "S" : "N";
        config.setAnonimizado(flag);

        return config;
    }

    public ConfigGestorDto toDto(ConfigGestor entity) {
        Long empresaId = entity.getEmpresa() != null ? entity.getEmpresa().getId() : null;
        Long timeId = entity.getTime() != null ? entity.getTime().getId() : null;
        Boolean anonimizado = "S".equalsIgnoreCase(entity.getAnonimizado());

        return new ConfigGestorDto(
                entity.getId(),
                empresaId,
                timeId,
                entity.getLimiarAlerta(),
                entity.getJanelaDias(),
                anonimizado
        );
    }
}
