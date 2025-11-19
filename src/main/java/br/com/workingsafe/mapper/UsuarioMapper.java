package br.com.workingsafe.mapper;

import br.com.workingsafe.dto.UsuarioDto;
import br.com.workingsafe.model.Usuario;
import br.com.workingsafe.model.Empresa;
import br.com.workingsafe.model.TimeEquipe;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public Usuario toEntity(UsuarioDto dto, Empresa empresa, TimeEquipe time) {
        Usuario usuario = new Usuario();
        usuario.setId(dto.id());
        usuario.setEmpresa(empresa);
        usuario.setTime(time);
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setFusoHorario(dto.fusoHorario());

        // ativo: Boolean no DTO -> 'S'/'N' no model
        String ativoFlag = (dto.ativo() == null || dto.ativo()) ? "S" : "N";
        usuario.setAtivo(ativoFlag);

        // senhaHash é tratada no Security / outro fluxo

        return usuario;
    }

    public UsuarioDto toDto(Usuario usuario) {
        Long timeId   = usuario.getTime() != null ? usuario.getTime().getId()   : null;
        String timeNm = usuario.getTime() != null ? usuario.getTime().getNome() : null;
        Boolean ativo = "S".equalsIgnoreCase(usuario.getAtivo());

        return new UsuarioDto(
                usuario.getId(),
                usuario.getEmpresa().getId(),
                timeId,
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getFusoHorario(),
                ativo,
                usuario.getEmpresa().getNome(),
                timeNm
        );
    }
}
