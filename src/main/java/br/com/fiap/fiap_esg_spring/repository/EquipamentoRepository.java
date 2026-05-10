package br.com.fiap.fiap_esg_spring.repository;

import br.com.fiap.fiap_esg_spring.model.Equipamento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipamentoRepository extends JpaRepository<Equipamento, Long> {

    List<Equipamento> findByEmpresaId(Long empresaId);
}
