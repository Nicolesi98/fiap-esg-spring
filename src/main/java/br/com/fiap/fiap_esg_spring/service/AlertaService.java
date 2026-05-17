package br.com.fiap.fiap_esg_spring.service;

import br.com.fiap.fiap_esg_spring.exception.ResourceNotFoundException;
import br.com.fiap.fiap_esg_spring.model.Alerta;
import br.com.fiap.fiap_esg_spring.model.AlertaStatus;
import br.com.fiap.fiap_esg_spring.repository.AlertaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlertaService {

    private final AlertaRepository repository;
    private final EquipamentoService equipamentoService;

    public List<Alerta> listar(Long equipamentoId, AlertaStatus status) {
        if (equipamentoId != null) {
            equipamentoService.verificaEquipamento(equipamentoId);
            return repository.findByEquipamentoId(equipamentoId);
        }
        if (status != null) {
            return repository.findByStatus(status);
        }
        return repository.findAll();
    }

    public Alerta buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta nao encontrado: " + id));
    }

    public Alerta criar(Alerta alerta) {
        alerta.setEquipamento(equipamentoService.buscarPorId(alerta.getEquipamento().getId()));
        return repository.save(alerta);
    }

    public Alerta atualizar(Long id, Alerta dados) {
        Alerta alerta = buscarPorId(id);
        alerta.setEquipamento(equipamentoService.buscarPorId(dados.getEquipamento().getId()));
        alerta.setDataHora(dados.getDataHora());
        alerta.setTipoAlerta(dados.getTipoAlerta());
        alerta.setMensagem(dados.getMensagem());
        alerta.setStatus(dados.getStatus());
        return repository.save(alerta);
    }

    public void excluir(Long id) {
        Alerta alerta = buscarPorId(id);
        repository.delete(alerta);
    }
}
