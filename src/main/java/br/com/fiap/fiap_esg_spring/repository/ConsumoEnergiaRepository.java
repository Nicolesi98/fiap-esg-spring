package br.com.fiap.fiap_esg_spring.repository;

import br.com.fiap.fiap_esg_spring.model.ConsumoEnergia;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsumoEnergiaRepository extends JpaRepository<ConsumoEnergia, Long> {

    List<ConsumoEnergia> findByEquipamentoId(Long equipamentoId);
}
