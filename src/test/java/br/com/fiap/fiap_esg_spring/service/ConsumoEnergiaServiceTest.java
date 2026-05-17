package br.com.fiap.fiap_esg_spring.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.fiap.fiap_esg_spring.model.ConsumoEnergia;
import br.com.fiap.fiap_esg_spring.model.Equipamento;
import br.com.fiap.fiap_esg_spring.repository.ConsumoEnergiaRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConsumoEnergiaServiceTest {

    @Mock
    private ConsumoEnergiaRepository repository;

    @Mock
    private EquipamentoService equipamentoService;

    @InjectMocks
    private ConsumoEnergiaService service;

    @Test
    void deveListarTodosQuandoEquipamentoNaoForInformado() {
        ConsumoEnergia consumo = consumo(1L, 1L);
        when(repository.findAll()).thenReturn(List.of(consumo));

        assertThat(service.listar(null)).containsExactly(consumo);
    }

    @Test
    void deveListarPorEquipamentoQuandoInformado() {
        ConsumoEnergia consumo = consumo(1L, 2L);
        when(repository.findByEquipamentoId(2L)).thenReturn(List.of(consumo));

        assertThat(service.listar(2L)).containsExactly(consumo);
        verify(equipamentoService).verificaEquipamento(2L);
    }

    @Test
    void deveCriarComEquipamentoExistente() {
        ConsumoEnergia consumo = consumo(null, 1L);
        Equipamento equipamento = equipamento(1L);
        when(equipamentoService.buscarPorId(1L)).thenReturn(equipamento);
        when(repository.save(consumo)).thenReturn(consumo);

        ConsumoEnergia criado = service.criar(consumo);

        assertThat(criado.getEquipamento()).isSameAs(equipamento);
        verify(repository).save(consumo);
    }

    @Test
    void deveAtualizarConsumoExistente() {
        ConsumoEnergia existente = consumo(1L, 1L);
        ConsumoEnergia dados = consumo(null, 2L);
        LocalDateTime dataHora = LocalDateTime.of(2026, 5, 17, 10, 0);
        dados.setDataHora(dataHora);
        dados.setConsumoKwh(new BigDecimal("99.90"));
        dados.setTempoOciosoMin(45);
        Equipamento equipamento = equipamento(2L);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(equipamentoService.buscarPorId(2L)).thenReturn(equipamento);
        when(repository.save(existente)).thenReturn(existente);

        ConsumoEnergia atualizado = service.atualizar(1L, dados);

        assertThat(atualizado.getEquipamento()).isSameAs(equipamento);
        assertThat(atualizado.getDataHora()).isEqualTo(dataHora);
        assertThat(atualizado.getConsumoKwh()).isEqualByComparingTo("99.90");
        assertThat(atualizado.getTempoOciosoMin()).isEqualTo(45);
    }

    private ConsumoEnergia consumo(Long id, Long equipamentoId) {
        ConsumoEnergia consumo = new ConsumoEnergia();
        consumo.setId(id);
        consumo.setEquipamento(equipamento(equipamentoId));
        consumo.setDataHora(LocalDateTime.of(2026, 5, 17, 9, 0));
        consumo.setConsumoKwh(new BigDecimal("10.00"));
        consumo.setTempoOciosoMin(5);
        return consumo;
    }

    private Equipamento equipamento(Long id) {
        Equipamento equipamento = new Equipamento();
        equipamento.setId(id);
        return equipamento;
    }
}
