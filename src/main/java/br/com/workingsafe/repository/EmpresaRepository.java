package br.com.workingsafe.repository;

import br.com.workingsafe.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    boolean existsByNome(String nome);
}
