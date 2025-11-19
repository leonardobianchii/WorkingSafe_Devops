package br.com.workingsafe.service;

import br.com.workingsafe.dto.RecomendacaoIaDto;
import br.com.workingsafe.mapper.RecomendacaoIaMapper;
import br.com.workingsafe.model.Checkin;
import br.com.workingsafe.model.RecomendacaoIa;
import br.com.workingsafe.model.Usuario;
import br.com.workingsafe.repository.CheckinRepository;
import br.com.workingsafe.repository.RecomendacaoIaRepository;
import br.com.workingsafe.repository.UsuarioRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;

@Service
public class RecomendacaoIaService {

    private final RecomendacaoIaRepository recomendacaoIaRepository;
    private final CheckinRepository checkinRepository;
    private final UsuarioRepository usuarioRepository;
    private final RecomendacaoIaMapper mapper;

    public RecomendacaoIaService(RecomendacaoIaRepository recomendacaoIaRepository,
                                 CheckinRepository checkinRepository,
                                 UsuarioRepository usuarioRepository,
                                 RecomendacaoIaMapper mapper) {
        this.recomendacaoIaRepository = recomendacaoIaRepository;
        this.checkinRepository = checkinRepository;
        this.usuarioRepository = usuarioRepository;
        this.mapper = mapper;
    }

    /**
     * Gera recomendacoes genericas com base nos ultimos 7 dias de check-ins
     * e salva na tabela T_GS_RECOMENDACAO_IA.
     */
    @Transactional
    public List<RecomendacaoIaDto> gerarRecomendacoesGenericas(Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado."));

        LocalDate hoje = LocalDate.now();
        LocalDate seteDiasAtras = hoje.minusDays(7);

        List<Checkin> checkins = checkinRepository
                .findByUsuarioIdAndDataHoraBetween(
                        usuarioId,
                        seteDiasAtras.atStartOfDay(),
                        hoje.atTime(23, 59, 59),
                        Pageable.unpaged()
                )
                .getContent();

        if (checkins.isEmpty()) {
            // sem check-in recente, nada a recomendar
            return List.of();
        }

        // --- calculo de medias ---
        DoubleSummaryStatistics statsHoras = checkins.stream()
                .filter(c -> c.getHorasTrabalhadas() != null)
                .mapToDouble(Checkin::getHorasTrabalhadas)
                .summaryStatistics();

        DoubleSummaryStatistics statsPausas = checkins.stream()
                .filter(c -> c.getMinutosPausas() != null)
                .mapToDouble(Checkin::getMinutosPausas)
                .summaryStatistics();

        DoubleSummaryStatistics statsHumor = checkins.stream()
                .filter(c -> c.getHumor() != null)
                .mapToDouble(Checkin::getHumor)
                .summaryStatistics();

        DoubleSummaryStatistics statsFoco = checkins.stream()
                .filter(c -> c.getFoco() != null)
                .mapToDouble(Checkin::getFoco)
                .summaryStatistics();

        double mediaHoras = statsHoras.getCount() > 0 ? statsHoras.getAverage() : 0;
        double mediaPausas = statsPausas.getCount() > 0 ? statsPausas.getAverage() : 0;
        double mediaHumor = statsHumor.getCount() > 0 ? statsHumor.getAverage() : 0;
        double mediaFoco = statsFoco.getCount() > 0 ? statsFoco.getAverage() : 0;

        // limpa recomendacoes antigas do usuario (para simplificar)
        recomendacaoIaRepository.deleteByUsuarioId(usuarioId);

        List<RecomendacaoIa> novas = new ArrayList<>();
        LocalDateTime agora = LocalDateTime.now();
        LocalDate dataValidade = hoje.plusDays(7);

        // ===== Regras simples =====

        // 1) Carga horaria alta
        if (mediaHoras >= 9.0) {
            RecomendacaoIa rec = new RecomendacaoIa();
            rec.setUsuario(usuario);
            rec.setCategoria("CARGA_ALTA");
            rec.setDescricao("""
                    Sua media de horas trabalhadas nos ultimos dias esta bem alta.
                    Tente alinhar prioridades com seu gestor, distribuir tarefas
                    e reservar blocos de descanso para evitar sobrecarga.
                    """.trim());
            rec.setDataCriacao(agora);
            rec.setDataValidade(dataValidade);
            rec.setOrigem("REGRAS_LOCAL");
            novas.add(rec);
        } else if (mediaHoras <= 6.0 && mediaHoras > 0) {
            RecomendacaoIa rec = new RecomendacaoIa();
            rec.setUsuario(usuario);
            rec.setCategoria("CARGA_BAIXA");
            rec.setDescricao("""
                    Sua media de horas trabalhadas esta relativamente baixa.
                    Verifique se voce tem tudo que precisa para executar suas atividades
                    e se nao ha bloqueios ou dependencias atrapalhando sua rotina.
                    """.trim());
            rec.setDataCriacao(agora);
            rec.setDataValidade(dataValidade);
            rec.setOrigem("REGRAS_LOCAL");
            novas.add(rec);
        }

        // 2) Pausas insuficientes
        if (mediaPausas < 20 && mediaHoras >= 8) {
            RecomendacaoIa rec = new RecomendacaoIa();
            rec.setUsuario(usuario);
            rec.setCategoria("POUCAS_PAUSAS");
            rec.setDescricao("""
                    Voce quase nao tem feito pausas ao longo do dia.
                    Tente incluir pequenas pausas a cada 60-90 minutos para se alongar,
                    beber agua ou desconectar alguns minutos da tela.
                    """.trim());
            rec.setDataCriacao(agora);
            rec.setDataValidade(dataValidade);
            rec.setOrigem("REGRAS_LOCAL");
            novas.add(rec);
        }

        // 3) Humor ou foco baixos
        if (mediaHumor > 0 && mediaHumor <= 2.0) {
            RecomendacaoIa rec = new RecomendacaoIa();
            rec.setUsuario(usuario);
            rec.setCategoria("HUMOR_BAIXO");
            rec.setDescricao("""
                    Seus registros de humor tem ficado baixos.
                    Observe o que esta impactando seu dia (carga, comunicacao, ambiente)
                    e, se possivel, converse com seu gestor ou time sobre isso.
                    """.trim());
            rec.setDataCriacao(agora);
            rec.setDataValidade(dataValidade);
            rec.setOrigem("REGRAS_LOCAL");
            novas.add(rec);
        }

        if (mediaFoco > 0 && mediaFoco <= 2.0) {
            RecomendacaoIa rec = new RecomendacaoIa();
            rec.setUsuario(usuario);
            rec.setCategoria("FOCO_BAIXO");
            rec.setDescricao("""
                    Sua media de foco esta baixa.
                    Tente agrupar tarefas semelhantes, reduzir notificacoes
                    e combinar periodos de concentracao com seu time.
                    """.trim());
            rec.setDataCriacao(agora);
            rec.setDataValidade(dataValidade);
            rec.setOrigem("REGRAS_LOCAL");
            novas.add(rec);
        }

        // 4) Caso tudo esteja ok, recomendacao positiva
        if (novas.isEmpty()) {
            RecomendacaoIa rec = new RecomendacaoIa();
            rec.setUsuario(usuario);
            rec.setCategoria("EQUILIBRADO");
            rec.setDescricao("""
                    Seus check-ins indicam uma rotina relativamente equilibrada.
                    Continue monitorando seu bem-estar e mantendo pausas e limites saudaveis
                    entre trabalho e descanso.
                    """.trim());
            rec.setDataCriacao(agora);
            rec.setDataValidade(dataValidade);
            rec.setOrigem("REGRAS_LOCAL");
            novas.add(rec);
        }

        novas = recomendacaoIaRepository.saveAll(novas);

        return novas.stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RecomendacaoIaDto> listarAtivasPorUsuario(Long usuarioId) {
        LocalDate hoje = LocalDate.now();

        // usa a coluna dt_validade >= hoje
        List<RecomendacaoIa> lista = recomendacaoIaRepository
                .findByUsuarioIdAndDataValidadeGreaterThanEqualOrderByDataCriacaoDesc(
                        usuarioId, hoje
                );

        return lista.stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RecomendacaoIaDto> listarTodasPorUsuario(Long usuarioId) {
        List<RecomendacaoIa> lista =
                recomendacaoIaRepository.findByUsuarioIdOrderByDataCriacaoDesc(usuarioId);
        return lista.stream()
                .map(mapper::toDto)
                .toList();
    }
}
