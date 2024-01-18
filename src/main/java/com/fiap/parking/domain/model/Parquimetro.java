package com.fiap.parking.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fiap.parking.domain.dto.ParquimetroDTO;
import com.fiap.parking.domain.dto.ParquimetroFixoDTO;
import com.fiap.parking.domain.dto.ParquimetroVariavelDTO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Entity
@Table(name = "tb_parquimetro")
public class Parquimetro {

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
    @Builder.Default
    private List<Periodo> periodos = new ArrayList<>();

    public ParquimetroVariavelDTO toVariavelDTO() {
        return new ParquimetroVariavelDTO(
            this.getId(),
            this.getVeiculo().getPlaca(),
            this.getCondutor().getCpf(),
            this.getLongitude(),
            this.getLatitude(),
            this.getValorHora(),
            this.getValorTotal(),
            this.getStatus(),
            this.getPeriodos().stream().map(Periodo::toDTO).collect(Collectors.toList())
        );
    }

    public ParquimetroFixoDTO toFixoDTO() {
        return new ParquimetroFixoDTO(
            this.getId(),
            this.getVeiculo().getPlaca(),
            this.getCondutor().getCpf(),
            this.getLongitude(),
            this.getLatitude(),
            this.getValorHora(),
            this.getValorTotal(),
            this.getStatus(),
            this.getPeriodos().stream().map(Periodo::toDTO).collect(Collectors.toList())
        );
    }

    public ParquimetroDTO toDTO() {
        if(tipoParquimetro == TipoParquimetro.FIXO){
            return toFixoDTO();
        }
        return toVariavelDTO();
    }
    
    public void encerrarTodosPeriodos() {
    	for (final Periodo periodo : this.getPeriodos()) {
            periodo.setAcaoPeriodo(AcaoPeriodo.ENCERRADO);
        }
    }
}
