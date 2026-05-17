package br.com.fiap.fiap_esg_spring.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.fiap.fiap_esg_spring.model.Alerta;
import br.com.fiap.fiap_esg_spring.model.AlertaStatus;
import br.com.fiap.fiap_esg_spring.model.Equipamento;
import br.com.fiap.fiap_esg_spring.repository.AlertaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AlertaServiceTest {

    @Mock
    private AlertaRepository repository;

    @Mock
    private EquipamentoService equipamentoService;

    @InjectMocks
    private AlertaService service;

    @Test
    void deveListarTodosQuandoFiltroNaoForInformado() {
        Alerta alerta = alerta(1L, 1L, AlertaStatus.ABERTO);
        when(repository.findAll()).thenReturn(List.of(alerta));

        assertThat(service.listar(null, null)).containsExactly(alerta);
    }

    @Test
    void deveListarPorEquipamentoComPrioridadeSobreStatus() {
        Alerta alerta = alerta(1L, 2L, AlertaStatus.RESOLVIDO);
        when(repository.findByEquipamentoId(2L)).thenReturn(List.of(alerta));

        assertThat(service.listar(2L, AlertaStatus.ABERTO)).containsExactly(alerta);
        verify(equipamentoService).verificaEquipamento(2L);
    }

    @Test
    void deveListarPorStatus() {
        Alerta alerta = alerta(1L, 1L, AlertaStatus.ABERTO);
        when(repository.findByStatus(AlertaStatus.ABERTO)).thenReturn(List.of(alerta));

        assertThat(service.listar(null, AlertaStatus.ABERTO)).containsExactly(alerta);
    }

    @Test
    void deveCriarComEquipamentoExistente() {
        Alerta alerta = alerta(null, 1L, AlertaStatus.ABERTO);
        Equipamento equipamento = equipamento(1L);
        when(equipamentoService.buscarPorId(1L)).thenReturn(equipamento);
        when(repository.save(alerta)).thenReturn(alerta);

        Alerta criado = service.criar(alerta);

        assertThat(criado.getEquipamento()).isSameAs(equipamento);
        verify(repository).save(alerta);
    }

    @Test
    void deveAtualizarAlertaExistente() {
        Alerta existente = alerta(1L, 1L, AlertaStatus.ABERTO);
        Alerta dados = alerta(null, 2L, AlertaStatus.RESOLVIDO);
        LocalDateTime dataHora = LocalDateTime.of(2026, 5, 17, 11, 0);
        dados.setDataHora(dataHora);
        dados.setTipoAlerta("OCIOSIDADE");
        dados.setMensagem("Tempo ocioso acima do limite.");
        Equipamento equipamento = equipamento(2L);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(equipamentoService.buscarPorId(2L)).thenReturn(equipamento);
        when(repository.save(existente)).thenReturn(existente);

        Alerta atualizado = service.atualizar(1L, dados);

        assertThat(atualizado.getEquipamento()).isSameAs(equipamento);
        assertThat(atualizado.getDataHora()).isEqualTo(dataHora);
        assertThat(atualizado.getTipoAlerta()).isEqualTo("OCIOSIDADE");
        assertThat(atualizado.getMensagem()).isEqualTo("Tempo ocioso acima do limite.");
        assertThat(atualizado.getStatus()).isEqualTo(AlertaStatus.RESOLVIDO);
    }

    private Alerta alerta(Long id, Long equipamentoId, AlertaStatus status) {
        Alerta alerta = new Alerta();
        alerta.setId(id);
        alerta.setEquipamento(equipamento(equipamentoId));
        alerta.setDataHora(LocalDateTime.of(2026, 5, 17, 9, 0));
        alerta.setTipoAlerta("CONSUMO_ALTO");
        alerta.setMensagem("Consumo acima do limite.");
        alerta.setStatus(status);
        return alerta;
    }

    private Equipamento equipamento(Long id) {
        Equipamento equipamento = new Equipamento();
        equipamento.setId(id);
        return equipamento;
    }
}
