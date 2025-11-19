package br.com.workingsafe.service;

import br.com.workingsafe.dto.CheckinDto;
import br.com.workingsafe.dto.RecomendacaoIaDto;
import br.com.workingsafe.mapper.CheckinMapper;
import br.com.workingsafe.model.Checkin;
import br.com.workingsafe.model.Usuario;
import br.com.workingsafe.repository.CheckinRepository;
import br.com.workingsafe.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;

@Service
public class CheckinService {

    private static final Logger log = LoggerFactory.getLogger(CheckinService.class);

    private final CheckinRepository checkinRepository;
    private final UsuarioRepository usuarioRepository;
    private final CheckinMapper mapper;
    private final RecomendacaoIaService recomendacaoIaService;

    public CheckinService(CheckinRepository checkinRepository,
                          UsuarioRepository usuarioRepository,
                          CheckinMapper mapper,
                          RecomendacaoIaService recomendacaoIaService) {
        this.checkinRepository = checkinRepository;
        this.usuarioRepository = usuarioRepository;
        this.mapper = mapper;
        this.recomendacaoIaService = recomendacaoIaService;
    }

    @Transactional
    public CheckinDto criar(CheckinDto dto) {
        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado."));

        LocalDate dia = dto.dataHora().toLocalDate();
        LocalDateTime inicio = dia.atStartOfDay();
        LocalDateTime fim = dia.atTime(23, 59, 59);

        boolean jaExiste = checkinRepository.existsByUsuarioIdAndDataHoraBetween(
                dto.usuarioId(), inicio, fim
        );

        if (jaExiste) {
            throw new IllegalArgumentException("Ja existe check-in para esse usuario neste dia.");
        }

        Checkin checkin = mapper.toEntity(dto, usuario);
        checkin = checkinRepository.save(checkin);


        // ===== Gera recomendacoes genericas com base nos ultimos check-ins =====
        List<RecomendacaoIaDto> recomendacoes =
                recomendacaoIaService.gerarRecomendacoesGenericas(usuario.getId());

        // Loga no console para demonstracao em video
        if (!recomendacoes.isEmpty()) {
            log.info("Recomendacoes geradas para usuario {} (id={}):",
                    usuario.getEmail(), usuario.getId());
            recomendacoes.forEach(r ->
                    log.info(" - [{}] {}", r.categoria(), r.descricao())
            );
        } else {
            log.info("Nenhuma recomendacao gerada para usuario {} (id={})",
                    usuario.getEmail(), usuario.getId());
        }

        return mapper.toDto(checkin);
    }

    @Transactional(readOnly = true)
    public CheckinDto buscarPorId(Long id) {
        Checkin checkin = checkinRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Check-in nao encontrado."));
        return mapper.toDto(checkin);
    }

    @Transactional(readOnly = true)
    public Page<CheckinDto> listarPorUsuario(Long usuarioId, Pageable pageable) {
        return checkinRepository.findByUsuarioId(usuarioId, pageable)
                .map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<CheckinDto> listarPorUsuarioEPeriodo(
            Long usuarioId,
            LocalDate from,
            LocalDate to,
            Pageable pageable
    ) {
        if (from == null || to == null) {
            return listarPorUsuario(usuarioId, pageable);
        }

        LocalDateTime inicio = from.atStartOfDay();
        LocalDateTime fim = to.atTime(23, 59, 59);

        return checkinRepository
                .findByUsuarioIdAndDataHoraBetween(usuarioId, inicio, fim, pageable)
                .map(mapper::toDto);
    }

    // =======================
    // NOVO METODO p/ HOME WEB
    // =======================
    @Transactional(readOnly = true)
    public java.util.List<CheckinDto> listarUltimosPorUsuario(Long usuarioId, int limite) {
        Pageable pageable = PageRequest.of(
                0,
                limite,
                Sort.by(Sort.Direction.DESC, "dataHora")
        );

        return checkinRepository.findByUsuarioId(usuarioId, pageable)
                .map(mapper::toDto)
                .getContent();
    }
}
