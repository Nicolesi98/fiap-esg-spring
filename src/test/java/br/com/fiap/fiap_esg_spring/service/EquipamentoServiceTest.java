package br.com.fiap.fiap_esg_spring.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.fiap.fiap_esg_spring.exception.ResourceNotFoundException;
import br.com.fiap.fiap_esg_spring.model.Empresa;
import br.com.fiap.fiap_esg_spring.model.Equipamento;
import br.com.fiap.fiap_esg_spring.model.EquipamentoStatus;
import br.com.fiap.fiap_esg_spring.repository.EquipamentoRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EquipamentoServiceTest {

    @Mock
    private EquipamentoRepository repository;

    @Mock
    private EmpresaService empresaService;

    @InjectMocks
    private EquipamentoService service;

    @Test
    void deveListarTodosQuandoEmpresaNaoForInformada() {
        Equipamento equipamento = equipamento(1L, 1L);
        when(repository.findAll()).thenReturn(List.of(equipamento));

        assertThat(service.listar(null)).containsExactly(equipamento);
    }

    @Test
    void deveListarPorEmpresaQuandoEmpresaForInformada() {
        Equipamento equipamento = equipamento(1L, 2L);
        when(repository.findByEmpresaId(2L)).thenReturn(List.of(equipamento));

        assertThat(service.listar(2L)).containsExactly(equipamento);
        verify(empresaService).verificaEmpresa(2L);
    }

    @Test
    void deveCriarComEmpresaExistente() {
        Equipamento equipamento = equipamento(null, 1L);
        Empresa empresa = empresa(1L);
        when(empresaService.buscarPorId(1L)).thenReturn(empresa);
        when(repository.save(equipamento)).thenReturn(equipamento);

        Equipamento criado = service.criar(equipamento);

        assertThat(criado.getEmpresa()).isSameAs(empresa);
        verify(repository).save(equipamento);
    }

    @Test
    void deveAtualizarEquipamentoExistente() {
        Equipamento existente = equipamento(1L, 1L);
        Equipamento dados = equipamento(null, 2L);
        dados.setNome("Sensor");
        dados.setTipo("MEDICAO");
        dados.setStatus(EquipamentoStatus.ATENCAO);
        dados.setLimiteConsumoKwh(new BigDecimal("12.50"));
        dados.setLimiteOciosidadeMin(7);
        Empresa empresa = empresa(2L);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(empresaService.buscarPorId(2L)).thenReturn(empresa);
        when(repository.save(existente)).thenReturn(existente);

        Equipamento atualizado = service.atualizar(1L, dados);

        assertThat(atualizado.getEmpresa()).isSameAs(empresa);
        assertThat(atualizado.getNome()).isEqualTo("Sensor");
        assertThat(atualizado.getStatus()).isEqualTo(EquipamentoStatus.ATENCAO);
        assertThat(atualizado.getLimiteConsumoKwh()).isEqualByComparingTo("12.50");
        assertThat(atualizado.getLimiteOciosidadeMin()).isEqualTo(7);
    }

    @Test
    void deveFalharQuandoEquipamentoNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Equipamento nao encontrado: 99");
    }

    private Equipamento equipamento(Long id, Long empresaId) {
        Equipamento equipamento = new Equipamento();
        equipamento.setId(id);
        equipamento.setEmpresa(empresa(empresaId));
        equipamento.setNome("Ar Condicionado");
        equipamento.setTipo("CLIMATIZACAO");
        equipamento.setStatus(EquipamentoStatus.LIGADO);
        equipamento.setLimiteConsumoKwh(new BigDecimal("50.00"));
        equipamento.setLimiteOciosidadeMin(30);
        return equipamento;
    }

    private Empresa empresa(Long id) {
        Empresa empresa = new Empresa();
        empresa.setId(id);
        empresa.setNome("Empresa " + id);
        return empresa;
    }
}
