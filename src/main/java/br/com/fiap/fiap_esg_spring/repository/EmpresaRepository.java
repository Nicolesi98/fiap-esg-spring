package br.com.fiap.fiap_esg_spring.repository;

import br.com.fiap.fiap_esg_spring.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
}
