package br.com.workingsafe.service;

import br.com.workingsafe.dto.UsuarioDto;
import br.com.workingsafe.mapper.UsuarioMapper;
import br.com.workingsafe.model.Usuario;
import br.com.workingsafe.model.Empresa;
import br.com.workingsafe.model.TimeEquipe;
import br.com.workingsafe.repository.EmpresaRepository;
import br.com.workingsafe.repository.TimeEquipeRepository;
import br.com.workingsafe.repository.UsuarioRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@CacheConfig(cacheNames = { "usuarioById", "usuarioByEmail", "usuariosPorEmpresaSemPaginacao" })
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final TimeEquipeRepository timeRepository;
    private final UsuarioMapper mapper;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          EmpresaRepository empresaRepository,
                          TimeEquipeRepository timeRepository,
                          UsuarioMapper mapper) {
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.timeRepository = timeRepository;
        this.mapper = mapper;
    }

    @Transactional
    @CacheEvict(allEntries = true) // limpamos caches relacionados a usuario
    public UsuarioDto criar(UsuarioDto dto) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Ja existe usuario com esse e-mail.");
        }

        Empresa empresa = empresaRepository.findById(dto.empresaId())
                .orElseThrow(() -> new IllegalArgumentException("Empresa nao encontrada."));

        TimeEquipe time = null;
        if (dto.timeId() != null) {
            time = timeRepository.findById(dto.timeId())
                    .orElseThrow(() -> new IllegalArgumentException("Time nao encontrado."));
        }

        Usuario usuario = mapper.toEntity(dto, empresa, time);
        // senha fixa por papel – nao mexemos aqui

        usuario = usuarioRepository.save(usuario);
        return mapper.toDto(usuario);
    }

    @Transactional
    @CacheEvict(allEntries = true) // qualquer update invalida os caches
    public UsuarioDto atualizar(Long id, UsuarioDto dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado."));

        if (dto.empresaId() != null) {
            Empresa empresa = empresaRepository.findById(dto.empresaId())
                    .orElseThrow(() -> new IllegalArgumentException("Empresa nao encontrada."));
            usuario.setEmpresa(empresa);
        }

        if (dto.timeId() != null) {
            TimeEquipe time = timeRepository.findById(dto.timeId())
                    .orElseThrow(() -> new IllegalArgumentException("Time nao encontrado."));
            usuario.setTime(time);
        } else {
            usuario.setTime(null);
        }

        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setFusoHorario(dto.fusoHorario());

        String ativoFlag = (dto.ativo() == null || dto.ativo()) ? "S" : "N";
        usuario.setAtivo(ativoFlag);

        usuario = usuarioRepository.save(usuario);
        return mapper.toDto(usuario);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "usuarioById", key = "#id")
    public UsuarioDto buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado."));
        return mapper.toDto(usuario);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioDto> listarPorEmpresa(Long empresaId, Pageable pageable) {
        // aqui eu NAO cacheei por causa do Pageable (a chave ficaria bem chata),
        // mas o requisito de cache ja fica bem coberto pelos metodos abaixo
        return usuarioRepository.findByEmpresaId(empresaId, pageable)
                .map(mapper::toDto);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void desativar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado."));
        usuario.setAtivo("N");
        usuarioRepository.save(usuario);
    }

    // ===== NOVOS METODOS PARA WEB/SECURITY =====

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "usuarioByEmail", key = "#email")
    public UsuarioDto buscarPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado com e-mail: " + email));
        return mapper.toDto(usuario);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "usuariosPorEmpresaSemPaginacao", key = "#empresaId")
    public List<UsuarioDto> listarPorEmpresaSemPaginacao(Long empresaId) {
        List<Usuario> usuarios = usuarioRepository.findByEmpresaId(empresaId);
        return usuarios.stream()
                .map(mapper::toDto)
                .toList();
    }
}
