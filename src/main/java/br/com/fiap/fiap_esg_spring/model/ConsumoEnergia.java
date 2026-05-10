package br.com.fiap.fiap_esg_spring.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "consumo_energia")
public class ConsumoEnergia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consumo")
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_equipamento", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "empresa"})
    private Equipamento equipamento;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "consumo_kwh", nullable = false, precision = 10, scale = 2)
    private BigDecimal consumoKwh;

    @NotNull
    @Min(0)
    @Column(name = "tempo_ocioso_min", nullable = false)
    private Integer tempoOciosoMin = 0;

    @PrePersist
    void preencherDataHora() {
        if (dataHora == null) {
            dataHora = LocalDateTime.now();
        }
    }
}
