package br.com.workingsafe.repository;

import br.com.workingsafe.model.RecomendacaoIa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RecomendacaoIaRepository extends JpaRepository<RecomendacaoIa, Long> {

    List<RecomendacaoIa> findByUsuarioIdAndDataValidadeGreaterThanEqualOrderByDataCriacaoDesc(
            Long usuarioId, LocalDate dataValidadeMin);

    List<RecomendacaoIa> findByUsuarioIdOrderByDataCriacaoDesc(Long usuarioId);

    void deleteByUsuarioId(Long usuarioId);
}
