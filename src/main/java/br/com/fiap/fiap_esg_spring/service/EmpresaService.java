package br.com.fiap.fiap_esg_spring.service;

import br.com.fiap.fiap_esg_spring.exception.ResourceNotFoundException;
import br.com.fiap.fiap_esg_spring.model.Empresa;
import br.com.fiap.fiap_esg_spring.repository.EmpresaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository repository;

    public List<Empresa> listar() {
        return repository.findAll();
    }

    public Empresa buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa nao encontrada: " + id));
    }

    protected void verificaEmpresa(Long id){
        if(!repository.existsById(id)){
            throw new ResourceNotFoundException("Empresa nao encontrada: " + id);
        }
    }

    public Empresa criar(Empresa empresa) {
        return repository.save(empresa);
    }

    public Empresa atualizar(Long id, Empresa dados) {
        Empresa empresa = buscarPorId(id);
        empresa.setNome(dados.getNome());
        empresa.setCnpj(dados.getCnpj());
        return repository.save(empresa);
    }

    public void excluir(Long id) {
        Empresa empresa = buscarPorId(id);
        repository.delete(empresa);
    }
}
