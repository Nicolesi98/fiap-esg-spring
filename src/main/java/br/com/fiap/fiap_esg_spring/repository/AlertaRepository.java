package br.com.fiap.fiap_esg_spring.repository;

import br.com.fiap.fiap_esg_spring.model.Alerta;
import br.com.fiap.fiap_esg_spring.model.AlertaStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    List<Alerta> findByEquipamentoId(Long equipamentoId);

    List<Alerta> findByStatus(AlertaStatus status);
}
