package br.com.workingsafe.mapper;

import br.com.workingsafe.dto.UsuarioPapelDto;
import br.com.workingsafe.model.Papel;
import br.com.workingsafe.model.Usuario;
import br.com.workingsafe.model.UsuarioPapel;
import org.springframework.stereotype.Component;

@Component
public class UsuarioPapelMapper {

    public UsuarioPapel toEntity(UsuarioPapelDto dto, Usuario usuario, Papel papel) {
        UsuarioPapel up = new UsuarioPapel();
        up.setId(dto.id());
        up.setUsuario(usuario);
        up.setPapel(papel);
        return up;
    }

    public UsuarioPapelDto toDto(UsuarioPapel entity) {
        Long usuarioId = entity.getUsuario() != null ? entity.getUsuario().getId() : null;
        Long papelId = entity.getPapel() != null ? entity.getPapel().getId() : null;

        return new UsuarioPapelDto(
                entity.getId(),
                usuarioId,
                papelId
        );
    }
}
