package br.com.workingsafe.repository;

import br.com.workingsafe.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmail(String email);

    Optional<Usuario> findByEmail(String email);

    Page<Usuario> findByEmpresaId(Long empresaId, Pageable pageable);

    // para o CRUD Thymeleaf (lista simples sem paginação)
    List<Usuario> findByEmpresaId(Long empresaId);
}
