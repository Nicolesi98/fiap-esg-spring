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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "alerta")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alerta")
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_equipamento", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "empresa"})
    private Equipamento equipamento;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @NotBlank
    @Size(max = 40)
    @Column(name = "tipo_alerta", nullable = false, length = 40)
    private String tipoAlerta;

    @NotBlank
    @Size(max = 200)
    @Column(name = "mensagem", nullable = false, length = 200)
    private String mensagem;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AlertaStatus status = AlertaStatus.ABERTO;

    @PrePersist
    void preencherDataHora() {
        if (dataHora == null) {
            dataHora = LocalDateTime.now();
        }
    }
}
