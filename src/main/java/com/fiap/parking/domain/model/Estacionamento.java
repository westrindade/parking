package com.fiap.parking.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "tb_estacionamento")
public class Estacionamento {

    public Estacionamento(){ }

    public Estacionamento(TipoTempo tipoTempo, String longitude, String latitude, BigDecimal valorHora, BigDecimal valorTotal, StatusEstacionamento status) {
        this.tipoTempo = tipoTempo;
        this.longitude = longitude;
        this.latitude = latitude;
        this.valorHora = valorHora;
        this.valorTotal = valorTotal;
        this.status = status;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="cd_estacionamento", unique = true)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "condutor_cpf",nullable = false)
    private Condutor condutor;

    @ManyToOne
    @JoinColumn(name = "veiculo_placa",nullable = false)
    private Veiculo veiculo;

    @NotNull(message = "O tipo de tempo precisa ser informado")
    @Enumerated(EnumType.STRING)
    @Column(name = "tp_tempo", nullable = false)
    private TipoTempo tipoTempo;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "vlr_hora")
    private BigDecimal valorHora;

    @Column(name = "vlr_total")
    private BigDecimal valorTotal;

    @Column(name = "status")
    private StatusEstacionamento status;

    @OneToMany(mappedBy = "estacionamento", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Periodo> periodos;
}
