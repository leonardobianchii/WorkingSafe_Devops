package br.com.workingsafe.service;

import br.com.workingsafe.dto.TimeEquipeDto;
import br.com.workingsafe.mapper.TimeEquipeMapper;
import br.com.workingsafe.model.Empresa;
import br.com.workingsafe.model.TimeEquipe;
import br.com.workingsafe.repository.EmpresaRepository;
import br.com.workingsafe.repository.TimeEquipeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TimeEquipeService {

    private final TimeEquipeRepository timeRepository;
    private final EmpresaRepository empresaRepository;
    private final TimeEquipeMapper mapper;

    public TimeEquipeService(TimeEquipeRepository timeRepository,
                             EmpresaRepository empresaRepository,
                             TimeEquipeMapper mapper) {
        this.timeRepository = timeRepository;
        this.empresaRepository = empresaRepository;
        this.mapper = mapper;
    }

    @Transactional
    public TimeEquipeDto criar(TimeEquipeDto dto) {
        Empresa empresa = empresaRepository.findById(dto.empresaId())
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada."));

        if (timeRepository.existsByEmpresaIdAndNome(dto.empresaId(), dto.nome())) {
            throw new IllegalArgumentException("Já existe um time com esse nome para esta empresa.");
        }

        TimeEquipe time = mapper.toEntity(dto, empresa);
        time = timeRepository.save(time);

        return mapper.toDto(time);
    }

    @Transactional
    public TimeEquipeDto atualizar(Long id, TimeEquipeDto dto) {
        TimeEquipe time = timeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Time não encontrado."));

        // Se mudou a empresa, carrega nova
        if (dto.empresaId() != null && !dto.empresaId().equals(time.getEmpresa().getId())) {
            Empresa empresa = empresaRepository.findById(dto.empresaId())
                    .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada."));
            time.setEmpresa(empresa);
        }

        // Valida nome duplicado dentro da mesma empresa
        Long empresaId = time.getEmpresa().getId();
        if (!time.getNome().equals(dto.nome()) &&
                timeRepository.existsByEmpresaIdAndNome(empresaId, dto.nome())) {
            throw new IllegalArgumentException("Já existe um time com esse nome para esta empresa.");
        }

        time.setNome(dto.nome());
        time.setDescricao(dto.descricao());

        time = timeRepository.save(time);
        return mapper.toDto(time);
    }

    @Transactional(readOnly = true)
    public TimeEquipeDto buscarPorId(Long id) {
        TimeEquipe time = timeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Time não encontrado."));
        return mapper.toDto(time);
    }

    @Transactional(readOnly = true)
    public Page<TimeEquipeDto> listarPorEmpresa(Long empresaId, Pageable pageable) {
        return timeRepository.findByEmpresaId(empresaId, pageable)
                .map(mapper::toDto);
    }

    @Transactional
    public void excluir(Long id) {
        if (!timeRepository.existsById(id)) {
            throw new IllegalArgumentException("Time não encontrado.");
        }
        timeRepository.deleteById(id);
    }

    // ============================
    // USADO NO UsuarioWebController
    // ============================
    @Transactional(readOnly = true)
    public List<TimeEquipeDto> listarPorEmpresa(Long empresaId) {
        // usa o método paginado internamente, mas sem limite
        return timeRepository.findByEmpresaId(empresaId, Pageable.unpaged())
                .map(mapper::toDto)
                .getContent();
    }
}
