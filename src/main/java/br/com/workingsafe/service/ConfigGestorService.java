package br.com.workingsafe.service;

import br.com.workingsafe.dto.ConfigGestorDto;
import br.com.workingsafe.mapper.ConfigGestorMapper;
import br.com.workingsafe.model.ConfigGestor;
import br.com.workingsafe.model.Empresa;
import br.com.workingsafe.model.TimeEquipe;
import br.com.workingsafe.repository.ConfigGestorRepository;
import br.com.workingsafe.repository.EmpresaRepository;
import br.com.workingsafe.repository.TimeEquipeRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfigGestorService {

    private final ConfigGestorRepository repository;
    private final EmpresaRepository empresaRepository;
    private final TimeEquipeRepository timeRepository;
    private final ConfigGestorMapper mapper;

    public ConfigGestorService(ConfigGestorRepository repository,
                               EmpresaRepository empresaRepository,
                               TimeEquipeRepository timeRepository,
                               ConfigGestorMapper mapper) {
        this.repository = repository;
        this.empresaRepository = empresaRepository;
        this.timeRepository = timeRepository;
        this.mapper = mapper;
    }

    @Transactional
    @CacheEvict(
            value = { "configGestorById", "configGestorPorEscopo" },
            allEntries = true
    )
    public ConfigGestorDto criar(ConfigGestorDto dto) {
        Empresa empresa = empresaRepository.findById(dto.empresaId())
                .orElseThrow(() -> new IllegalArgumentException("Empresa nao encontrada."));

        TimeEquipe time = null;
        if (dto.timeId() != null) {
            time = timeRepository.findById(dto.timeId())
                    .orElseThrow(() -> new IllegalArgumentException("Time nao encontrado."));
        }

        // valida se ja existe config para esse escopo
        if (dto.timeId() == null) {
            repository.findByEmpresaIdAndTimeIsNull(dto.empresaId())
                    .ifPresent(c -> {
                        throw new IllegalArgumentException("Ja existe configuracao padrao para essa empresa.");
                    });
        } else {
            repository.findByEmpresaIdAndTimeId(dto.empresaId(), dto.timeId())
                    .ifPresent(c -> {
                        throw new IllegalArgumentException("Ja existe configuracao para essa empresa e time.");
                    });
        }

        ConfigGestor config = mapper.toEntity(dto, empresa, time);
        config = repository.save(config);

        return mapper.toDto(config);
    }

    @Transactional
    @CacheEvict(
            value = { "configGestorById", "configGestorPorEscopo" },
            allEntries = true
    )
    public ConfigGestorDto atualizar(Long id, ConfigGestorDto dto) {
        ConfigGestor existente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Configuracao nao encontrada."));

        Empresa empresa = empresaRepository.findById(dto.empresaId())
                .orElseThrow(() -> new IllegalArgumentException("Empresa nao encontrada."));

        TimeEquipe time = null;
        if (dto.timeId() != null) {
            time = timeRepository.findById(dto.timeId())
                    .orElseThrow(() -> new IllegalArgumentException("Time nao encontrado."));
        }

        // se mudar de escopo, valida duplicidade
        Long timeIdExistente = existente.getTime() != null ? existente.getTime().getId() : null;
        boolean mudouEscopo =
                !existente.getEmpresa().getId().equals(dto.empresaId()) ||
                        (timeIdExistente == null ? dto.timeId() != null : !timeIdExistente.equals(dto.timeId()));

        if (mudouEscopo) {
            if (dto.timeId() == null) {
                repository.findByEmpresaIdAndTimeIsNull(dto.empresaId())
                        .ifPresent(c -> {
                            throw new IllegalArgumentException("Ja existe configuracao padrao para essa empresa.");
                        });
            } else {
                repository.findByEmpresaIdAndTimeId(dto.empresaId(), dto.timeId())
                        .ifPresent(c -> {
                            throw new IllegalArgumentException("Ja existe configuracao para essa empresa e time.");
                        });
            }
        }

        existente.setEmpresa(empresa);
        existente.setTime(time);
        existente.setLimiarAlerta(dto.limiarAlerta());
        existente.setJanelaDias(dto.janelaDias());
        existente.setAnonimizado(dto.anonimizado() != null && dto.anonimizado() ? "S" : "N");

        existente = repository.save(existente);
        return mapper.toDto(existente);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "configGestorById", key = "#id")
    public ConfigGestorDto buscarPorId(Long id) {
        ConfigGestor config = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Configuracao nao encontrada."));
        return mapper.toDto(config);
    }

    @Transactional(readOnly = true)
    @Cacheable(
            value = "configGestorPorEscopo",
            key = "#empresaId + ':' + (#timeId != null ? #timeId : 'DEFAULT')"
    )
    public ConfigGestorDto buscarPorEscopo(Long empresaId, Long timeId) {
        ConfigGestor config;

        if (timeId == null) {
            config = repository.findByEmpresaIdAndTimeIsNull(empresaId)
                    .orElseThrow(() -> new IllegalArgumentException("Configuracao padrao nao encontrada."));
        } else {
            config = repository.findByEmpresaIdAndTimeId(empresaId, timeId)
                    .orElseThrow(() -> new IllegalArgumentException("Configuracao para empresa/time nao encontrada."));
        }
        return mapper.toDto(config);
    }

    @Transactional
    @CacheEvict(
            value = { "configGestorById", "configGestorPorEscopo" },
            allEntries = true
    )
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Configuracao nao encontrada.");
        }
        repository.deleteById(id);
    }
}
