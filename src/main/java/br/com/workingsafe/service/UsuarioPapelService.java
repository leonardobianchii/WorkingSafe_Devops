package br.com.workingsafe.service;

import br.com.workingsafe.dto.UsuarioPapelDto;
import br.com.workingsafe.mapper.UsuarioPapelMapper;
import br.com.workingsafe.model.Papel;
import br.com.workingsafe.model.Usuario;
import br.com.workingsafe.model.UsuarioPapel;
import br.com.workingsafe.repository.PapelRepository;
import br.com.workingsafe.repository.UsuarioPapelRepository;
import br.com.workingsafe.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioPapelService {

    private final UsuarioPapelRepository usuarioPapelRepository;
    private final UsuarioRepository usuarioRepository;
    private final PapelRepository papelRepository;
    private final UsuarioPapelMapper mapper;

    public UsuarioPapelService(UsuarioPapelRepository usuarioPapelRepository,
                               UsuarioRepository usuarioRepository,
                               PapelRepository papelRepository,
                               UsuarioPapelMapper mapper) {
        this.usuarioPapelRepository = usuarioPapelRepository;
        this.usuarioRepository = usuarioRepository;
        this.papelRepository = papelRepository;
        this.mapper = mapper;
    }

    @Transactional
    public UsuarioPapelDto atribuirPapel(UsuarioPapelDto dto) {
        if (usuarioPapelRepository.existsByUsuarioIdAndPapelId(dto.usuarioId(), dto.papelId())) {
            throw new IllegalArgumentException("Esse usuário já possui esse papel.");
        }

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        Papel papel = papelRepository.findById(dto.papelId())
                .orElseThrow(() -> new IllegalArgumentException("Papel não encontrado."));

        UsuarioPapel entity = mapper.toEntity(dto, usuario, papel);
        entity = usuarioPapelRepository.save(entity);

        return mapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioPapelDto> listarPapeisDoUsuario(Long usuarioId, Pageable pageable) {
        return usuarioPapelRepository.findByUsuarioId(usuarioId, pageable)
                .map(mapper::toDto);
    }

    @Transactional
    public void removerPapel(Long id) {
        if (!usuarioPapelRepository.existsById(id)) {
            throw new IllegalArgumentException("Relação usuário-papel não encontrada.");
        }
        usuarioPapelRepository.deleteById(id);
    }
}
