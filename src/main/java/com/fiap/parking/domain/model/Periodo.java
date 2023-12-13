package com.fiap.parking.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "tb_estacionamento_periodo")
public class Periodo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="cd_estacionamento_periodo", unique = true)
    private UUID idPeriodo;
    @Column(name = "dt_hr_inicial")
    LocalDateTime dataHoraInicial;
    @Column(name = "dt_hr_final")
    LocalDateTime dataHoraFinal;
    @Column(name = "acao_Periodo")
    AcaoPeriodo acaoPeriodo;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "cd_estacionamento", referencedColumnName = "cd_estacionamento", nullable = true)
    private Estacionamento estacionamento;
}
