package br.com.workingsafe.repository;

import br.com.workingsafe.model.UsuarioPapel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioPapelRepository extends JpaRepository<UsuarioPapel, Long> {

    boolean existsByUsuarioIdAndPapelId(Long usuarioId, Long papelId);

    // Para telas (paginado)
    Page<UsuarioPapel> findByUsuarioId(Long usuarioId, Pageable pageable);

    // Para o Security (sem paginação)
    List<UsuarioPapel> findByUsuarioId(Long usuarioId);
}
