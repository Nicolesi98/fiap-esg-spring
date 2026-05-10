package br.com.fiap.fiap_esg_spring.service;

import br.com.fiap.fiap_esg_spring.exception.ResourceNotFoundException;
import br.com.fiap.fiap_esg_spring.model.ConsumoEnergia;
import br.com.fiap.fiap_esg_spring.repository.ConsumoEnergiaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConsumoEnergiaService {

    private final ConsumoEnergiaRepository repository;
    private final EquipamentoService equipamentoService;

    public List<ConsumoEnergia> listar(Long equipamentoId) {
        if (equipamentoId == null) {
            return repository.findAll();
        }
        equipamentoService.buscarPorId(equipamentoId);
        return repository.findByEquipamentoId(equipamentoId);
    }

    public ConsumoEnergia buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consumo de energia nao encontrado: " + id));
    }

    public ConsumoEnergia criar(ConsumoEnergia consumo) {
        consumo.setId(null);
        consumo.setEquipamento(equipamentoService.buscarPorId(consumo.getEquipamento().getId()));
        return repository.save(consumo);
    }

    public ConsumoEnergia atualizar(Long id, ConsumoEnergia dados) {
        ConsumoEnergia consumo = buscarPorId(id);
        consumo.setEquipamento(equipamentoService.buscarPorId(dados.getEquipamento().getId()));
        consumo.setDataHora(dados.getDataHora());
        consumo.setConsumoKwh(dados.getConsumoKwh());
        consumo.setTempoOciosoMin(dados.getTempoOciosoMin());
        return repository.save(consumo);
    }

    public void excluir(Long id) {
        ConsumoEnergia consumo = buscarPorId(id);
        repository.delete(consumo);
    }
}
