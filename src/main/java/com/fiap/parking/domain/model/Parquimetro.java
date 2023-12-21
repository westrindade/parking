package com.fiap.parking.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.fiap.parking.domain.dto.ParquimetroDTO;

@Data
@Entity
@Table(name = "tb_parquimetro")
public class Parquimetro {

    public Parquimetro(){ }

    public Parquimetro(TipoParquimetro tipoParquimetro, String longitude, String latitude, BigDecimal valorHora, BigDecimal valorTotal, StatusParquimetro status) {
        this.tipoParquimetro = tipoParquimetro;
        this.longitude = longitude;
        this.latitude = latitude;
        this.valorHora = valorHora;
        this.valorTotal = valorTotal;
        this.status = status;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="cd_parquimetro", unique = true)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "condutor_cpf",nullable = false)
    private Condutor condutor;

    @ManyToOne
    @JoinColumn(name = "veiculo_placa",nullable = false)
    private Veiculo veiculo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipoParquimetro", nullable = false)
    private TipoParquimetro tipoParquimetro;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "vlr_hora")
    private BigDecimal valorHora;

    @Column(name = "vlr_total")
    private BigDecimal valorTotal;

    @Column(name = "status")
    private StatusParquimetro status;

    @OneToMany(mappedBy = "parquimetro", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Periodo> periodos;

    public ParquimetroDTO toDTO() {
        return new ParquimetroDTO(
            this.getId(),
            this.getTipoParquimetro(),
            this.getVeiculo().getPlaca(),
            this.getCondutor().getCpf(),
            this.getLongitude(),
            this.getLatitude(),
            this.getValorHora(),
            this.getValorTotal(),
            this.getStatus(),
            this.getPeriodos()
        );
    }
}
