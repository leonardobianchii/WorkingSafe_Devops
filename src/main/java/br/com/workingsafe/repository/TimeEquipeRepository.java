package br.com.workingsafe.repository;

import br.com.workingsafe.model.TimeEquipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeEquipeRepository extends JpaRepository<TimeEquipe, Long> {

    Page<TimeEquipe> findByEmpresaId(Long empresaId, Pageable pageable);

    boolean existsByEmpresaIdAndNome(Long empresaId, String nome);
}
