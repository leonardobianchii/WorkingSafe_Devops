package br.com.workingsafe.repository;

import br.com.workingsafe.model.AggIndiceSemanal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface AggIndiceSemanalRepository extends JpaRepository<AggIndiceSemanal, Long> {

    Page<AggIndiceSemanal> findByEmpresaId(Long empresaId, Pageable pageable);

    Page<AggIndiceSemanal> findByEmpresaIdAndInicioSemanaBetween(
            Long empresaId,
            LocalDate inicio,
            LocalDate fim,
            Pageable pageable
    );

    Page<AggIndiceSemanal> findByEmpresaIdAndTimeIdAndInicioSemanaBetween(
            Long empresaId,
            Long timeId,
            LocalDate inicio,
            LocalDate fim,
            Pageable pageable
    );
}
