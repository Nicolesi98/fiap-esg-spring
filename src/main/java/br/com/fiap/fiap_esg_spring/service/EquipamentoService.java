package br.com.fiap.fiap_esg_spring.service;

import br.com.fiap.fiap_esg_spring.exception.ResourceNotFoundException;
import br.com.fiap.fiap_esg_spring.model.Equipamento;
import br.com.fiap.fiap_esg_spring.repository.EquipamentoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EquipamentoService {

    private final EquipamentoRepository repository;
    private final EmpresaService empresaService;

    public List<Equipamento> listar(Long empresaId) {
        if (empresaId == null) {
            return repository.findAll();
        }
        empresaService.buscarPorId(empresaId);
        return repository.findByEmpresaId(empresaId);
    }

    public Equipamento buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipamento nao encontrado: " + id));
    }

    public Equipamento criar(Equipamento equipamento) {
        equipamento.setId(null);
        equipamento.setEmpresa(empresaService.buscarPorId(equipamento.getEmpresa().getId()));
        return repository.save(equipamento);
    }

    public Equipamento atualizar(Long id, Equipamento dados) {
        Equipamento equipamento = buscarPorId(id);
        equipamento.setEmpresa(empresaService.buscarPorId(dados.getEmpresa().getId()));
        equipamento.setNome(dados.getNome());
        equipamento.setTipo(dados.getTipo());
        equipamento.setStatus(dados.getStatus());
        equipamento.setLimiteConsumoKwh(dados.getLimiteConsumoKwh());
        equipamento.setLimiteOciosidadeMin(dados.getLimiteOciosidadeMin());
        return repository.save(equipamento);
    }

    public void excluir(Long id) {
        Equipamento equipamento = buscarPorId(id);
        repository.delete(equipamento);
    }
}
