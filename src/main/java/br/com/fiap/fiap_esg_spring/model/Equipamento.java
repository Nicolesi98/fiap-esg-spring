package br.com.fiap.fiap_esg_spring.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "equipamento")
public class Equipamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_equipamento")
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_empresa", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Empresa empresa;

    @NotBlank
    @Size(max = 100)
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Size(max = 50)
    @Column(name = "tipo", length = 50)
    private String tipo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EquipamentoStatus status = EquipamentoStatus.LIGADO;

    @NotNull
    @DecimalMin("0.01")
    @Column(name = "limite_consumo_kwh", nullable = false, precision = 10, scale = 2)
    private BigDecimal limiteConsumoKwh;

    @NotNull
    @Min(0)
    @Column(name = "limite_ociosidade_min", nullable = false)
    private Integer limiteOciosidadeMin;
}
