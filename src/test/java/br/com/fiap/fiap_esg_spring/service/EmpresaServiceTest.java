package br.com.fiap.fiap_esg_spring.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.fiap.fiap_esg_spring.exception.ResourceNotFoundException;
import br.com.fiap.fiap_esg_spring.model.Empresa;
import br.com.fiap.fiap_esg_spring.repository.EmpresaRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceTest {

    @Mock
    private EmpresaRepository repository;

    @InjectMocks
    private EmpresaService service;

    @Test
    void deveListarEmpresas() {
        Empresa empresa = empresa(1L, "EcoTech", "11");
        when(repository.findAll()).thenReturn(List.of(empresa));

        assertThat(service.listar()).containsExactly(empresa);
    }

    @Test
    void deveBuscarPorId() {
        Empresa empresa = empresa(1L, "EcoTech", "11");
        when(repository.findById(1L)).thenReturn(Optional.of(empresa));

        assertThat(service.buscarPorId(1L)).isSameAs(empresa);
    }

    @Test
    void deveFalharQuandoEmpresaNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Empresa nao encontrada: 99");
    }

    @Test
    void deveAtualizarEmpresaExistente() {
        Empresa existente = empresa(1L, "Antiga", "11");
        Empresa dados = empresa(null, "Nova", "22");
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(existente)).thenReturn(existente);

        Empresa atualizada = service.atualizar(1L, dados);

        assertThat(atualizada.getNome()).isEqualTo("Nova");
        assertThat(atualizada.getCnpj()).isEqualTo("22");
        verify(repository).save(existente);
    }

    @Test
    void deveExcluirEmpresaExistente() {
        Empresa empresa = empresa(1L, "EcoTech", "11");
        when(repository.findById(1L)).thenReturn(Optional.of(empresa));

        service.excluir(1L);

        verify(repository).delete(empresa);
    }

    private Empresa empresa(Long id, String nome, String cnpj) {
        Empresa empresa = new Empresa();
        empresa.setId(id);
        empresa.setNome(nome);
        empresa.setCnpj(cnpj);
        return empresa;
    }
}
