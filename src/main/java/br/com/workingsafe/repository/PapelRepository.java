package br.com.workingsafe.repository;

import br.com.workingsafe.model.Papel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PapelRepository extends JpaRepository<Papel, Long> {

    boolean existsByCodigo(String codigo);

    Optional<Papel> findByCodigo(String codigo);
}
