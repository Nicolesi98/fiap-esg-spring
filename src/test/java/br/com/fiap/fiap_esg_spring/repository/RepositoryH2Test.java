package br.com.fiap.fiap_esg_spring.repository;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.fiap.fiap_esg_spring.model.Alerta;
import br.com.fiap.fiap_esg_spring.model.AlertaStatus;
import br.com.fiap.fiap_esg_spring.model.ConsumoEnergia;
import br.com.fiap.fiap_esg_spring.model.Empresa;
import br.com.fiap.fiap_esg_spring.model.Equipamento;
import br.com.fiap.fiap_esg_spring.model.EquipamentoStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:repositorytest;MODE=Oracle;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.flyway.enabled=false",
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class RepositoryH2Test {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private EquipamentoRepository equipamentoRepository;

    @Autowired
    private ConsumoEnergiaRepository consumoEnergiaRepository;

    @Autowired
    private AlertaRepository alertaRepository;

    @Test
    void deveSalvarEBuscarEmpresaNoH2() {
        Empresa empresa = empresa("EcoTech Teste", "11.111.111/0001-11");

        Empresa salva = empresaRepository.saveAndFlush(empresa);

        assertThat(salva.getId()).isNotNull();
        assertThat(empresaRepository.findById(salva.getId()))
                .isPresent()
                .get()
                .extracting(Empresa::getNome)
                .isEqualTo("EcoTech Teste");
    }

    @Test
    void deveBuscarEquipamentosPorEmpresaNoH2() {
        Empresa empresaUm = empresaRepository.saveAndFlush(empresa("Empresa Um", "11"));
        Empresa empresaDois = empresaRepository.saveAndFlush(empresa("Empresa Dois", "22"));
        Equipamento equipamentoUm = equipamentoRepository.saveAndFlush(equipamento(empresaUm, "Ar Condicionado"));
        equipamentoRepository.saveAndFlush(equipamento(empresaDois, "Iluminacao"));

        assertThat(equipamentoRepository.findByEmpresaId(empresaUm.getId()))
                .containsExactly(equipamentoUm);
    }

    @Test
    void deveBuscarConsumosPorEquipamentoNoH2() {
        Empresa empresa = empresaRepository.saveAndFlush(empresa("Empresa", "11"));
        Equipamento equipamentoUm = equipamentoRepository.saveAndFlush(equipamento(empresa, "Ar Condicionado"));
        Equipamento equipamentoDois = equipamentoRepository.saveAndFlush(equipamento(empresa, "Iluminacao"));
        ConsumoEnergia consumo = consumoEnergiaRepository.saveAndFlush(consumo(equipamentoUm, "35.50", 10));
        consumoEnergiaRepository.saveAndFlush(consumo(equipamentoDois, "12.00", 5));

        assertThat(consumoEnergiaRepository.findByEquipamentoId(equipamentoUm.getId()))
                .containsExactly(consumo);
    }

    @Test
    void deveBuscarAlertasPorEquipamentoEStatusNoH2() {
        Empresa empresa = empresaRepository.saveAndFlush(empresa("Empresa", "11"));
        Equipamento equipamentoUm = equipamentoRepository.saveAndFlush(equipamento(empresa, "Ar Condicionado"));
        Equipamento equipamentoDois = equipamentoRepository.saveAndFlush(equipamento(empresa, "Iluminacao"));
        Alerta alertaAberto = alertaRepository.saveAndFlush(alerta(equipamentoUm, AlertaStatus.ABERTO));
        Alerta alertaResolvido = alertaRepository.saveAndFlush(alerta(equipamentoDois, AlertaStatus.RESOLVIDO));

        assertThat(alertaRepository.findByEquipamentoId(equipamentoUm.getId()))
                .containsExactly(alertaAberto);
        assertThat(alertaRepository.findByStatus(AlertaStatus.RESOLVIDO))
                .containsExactly(alertaResolvido);
    }

    private Empresa empresa(String nome, String cnpj) {
        Empresa empresa = new Empresa();
        empresa.setNome(nome);
        empresa.setCnpj(cnpj);
        return empresa;
    }

    private Equipamento equipamento(Empresa empresa, String nome) {
        Equipamento equipamento = new Equipamento();
        equipamento.setEmpresa(empresa);
        equipamento.setNome(nome);
        equipamento.setTipo("TESTE");
        equipamento.setStatus(EquipamentoStatus.LIGADO);
        equipamento.setLimiteConsumoKwh(new BigDecimal("50.00"));
        equipamento.setLimiteOciosidadeMin(30);
        return equipamento;
    }

    private ConsumoEnergia consumo(Equipamento equipamento, String consumoKwh, Integer tempoOciosoMin) {
        ConsumoEnergia consumo = new ConsumoEnergia();
        consumo.setEquipamento(equipamento);
        consumo.setDataHora(LocalDateTime.of(2026, 5, 17, 12, 0));
        consumo.setConsumoKwh(new BigDecimal(consumoKwh));
        consumo.setTempoOciosoMin(tempoOciosoMin);
        return consumo;
    }

    private Alerta alerta(Equipamento equipamento, AlertaStatus status) {
        Alerta alerta = new Alerta();
        alerta.setEquipamento(equipamento);
        alerta.setDataHora(LocalDateTime.of(2026, 5, 17, 13, 0));
        alerta.setTipoAlerta("TESTE");
        alerta.setMensagem("Alerta de teste");
        alerta.setStatus(status);
        return alerta;
    }
}
