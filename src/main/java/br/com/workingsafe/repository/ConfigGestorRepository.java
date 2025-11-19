package br.com.workingsafe.repository;

import br.com.workingsafe.model.ConfigGestor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfigGestorRepository extends JpaRepository<ConfigGestor, Long> {

    Optional<ConfigGestor> findByEmpresaIdAndTimeIsNull(Long empresaId);

    Optional<ConfigGestor> findByEmpresaIdAndTimeId(Long empresaId, Long timeId);
}
