package br.com.workingsafe.repository;

import br.com.workingsafe.model.Checkin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface CheckinRepository extends JpaRepository<Checkin, Long> {

    Page<Checkin> findByUsuarioId(Long usuarioId, Pageable pageable);

    Page<Checkin> findByUsuarioIdAndDataHoraBetween(
            Long usuarioId,
            LocalDateTime inicio,
            LocalDateTime fim,
            Pageable pageable
    );

    boolean existsByUsuarioIdAndDataHoraBetween(
            Long usuarioId,
            LocalDateTime inicio,
            LocalDateTime fim
    );
}
