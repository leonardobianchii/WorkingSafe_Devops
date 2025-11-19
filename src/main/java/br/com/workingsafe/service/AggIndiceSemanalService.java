package br.com.workingsafe.service;

import br.com.workingsafe.dto.AggIndiceSemanalDto;
import br.com.workingsafe.mapper.AggIndiceSemanalMapper;
import br.com.workingsafe.model.AggIndiceSemanal;
import br.com.workingsafe.model.Empresa;
import br.com.workingsafe.model.TimeEquipe;
import br.com.workingsafe.repository.AggIndiceSemanalRepository;
import br.com.workingsafe.repository.EmpresaRepository;
import br.com.workingsafe.repository.TimeEquipeRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

@Service
public class AggIndiceSemanalService {

    private final AggIndiceSemanalRepository repository;
    private final EmpresaRepository empresaRepository;
    private final TimeEquipeRepository timeRepository;
    private final AggIndiceSemanalMapper mapper;

    public AggIndiceSemanalService(AggIndiceSemanalRepository repository,
                                   EmpresaRepository empresaRepository,
                                   TimeEquipeRepository timeRepository,
                                   AggIndiceSemanalMapper mapper) {
        this.repository = repository;
        this.empresaRepository = empresaRepository;
        this.timeRepository = timeRepository;
        this.mapper = mapper;
    }

    @Transactional
    @CacheEvict(
            value = {
                    "aggById",
                    "aggUltimasSemanasPorEmpresa",
                    "aggMediaIndiceSemanaAtual",
                    "aggMediaRiscoSemanaAtual",
                    "aggAdesaoSemanaAtual"
            },
            allEntries = true
    )
    public AggIndiceSemanalDto criar(AggIndiceSemanalDto dto) {
        Empresa empresa = empresaRepository.findById(dto.empresaId())
                .orElseThrow(() -> new IllegalArgumentException("Empresa nao encontrada."));

        TimeEquipe time = null;
        if (dto.timeId() != null) {
            time = timeRepository.findById(dto.timeId())
                    .orElseThrow(() -> new IllegalArgumentException("Time nao encontrado."));
        }

        AggIndiceSemanal agg = mapper.toEntity(dto, empresa, time);
        agg = repository.save(agg);

        return mapper.toDto(agg);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "aggById", key = "#id")
    public AggIndiceSemanalDto buscarPorId(Long id) {
        AggIndiceSemanal agg = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Registro nao encontrado."));
        return mapper.toDto(agg);
    }

    @Transactional(readOnly = true)
    public Page<AggIndiceSemanalDto> listarPorEmpresaEPeriodo(
            Long empresaId,
            Long timeId,
            LocalDate from,
            LocalDate to,
            Pageable pageable) {

        if (from == null || to == null) {
            return repository.findByEmpresaId(empresaId, pageable)
                    .map(mapper::toDto);
        }

        if (timeId == null) {
            return repository.findByEmpresaIdAndInicioSemanaBetween(
                    empresaId, from, to, pageable
            ).map(mapper::toDto);
        } else {
            return repository.findByEmpresaIdAndTimeIdAndInicioSemanaBetween(
                    empresaId, timeId, from, to, pageable
            ).map(mapper::toDto);
        }
    }

    // =========================
    // METRICAS PARA DASHBOARD
    // =========================

    @Transactional(readOnly = true)
    @Cacheable(
            value = "aggUltimasSemanasPorEmpresa",
            key = "#empresaId + ':' + #limite"
    )
    public List<AggIndiceSemanalDto> listarUltimasSemanasPorEmpresa(Long empresaId, int limite) {
        Pageable pageable = PageRequest.of(
                0,
                limite,
                Sort.by(Sort.Direction.DESC, "inicioSemana")
        );

        return repository.findByEmpresaId(empresaId, pageable)
                .map(mapper::toDto)
                .getContent();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "aggMediaIndiceSemanaAtual", key = "#empresaId")
    public Double calcularMediaIndiceSemanaAtual(Long empresaId) {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioSemana = hoje.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);

        List<AggIndiceSemanal> lista = repository
                .findByEmpresaIdAndInicioSemanaBetween(
                        empresaId, inicioSemana, inicioSemana, Pageable.unpaged()
                ).getContent();

        return lista.stream()
                .mapToDouble(AggIndiceSemanal::getMediaIndice)
                .average()
                .orElse(0.0);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "aggMediaRiscoSemanaAtual", key = "#empresaId")
    public Double calcularMediaRiscoSemanaAtual(Long empresaId) {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioSemana = hoje.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);

        List<AggIndiceSemanal> lista = repository
                .findByEmpresaIdAndInicioSemanaBetween(
                        empresaId, inicioSemana, inicioSemana, Pageable.unpaged()
                ).getContent();

        return lista.stream()
                .mapToDouble(AggIndiceSemanal::getMediaRisco)
                .average()
                .orElse(0.0);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "aggAdesaoSemanaAtual", key = "#empresaId")
    public Integer calcularAdesaoSemanaAtual(Long empresaId) {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioSemana = hoje.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);

        List<AggIndiceSemanal> lista = repository
                .findByEmpresaIdAndInicioSemanaBetween(
                        empresaId, inicioSemana, inicioSemana, Pageable.unpaged()
                ).getContent();

        return lista.stream()
                .mapToInt(AggIndiceSemanal::getQtdUsuarios)
                .sum();
    }
}
