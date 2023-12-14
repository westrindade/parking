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
    private UUID id;
    @Column(name = "dt_hr_inicial")
    LocalDateTime dataHoraInicial;
    @Column(name = "dt_hr_final")
    LocalDateTime dataHoraFinal;
    @Column(name = "acao_Periodo")
    AcaoPeriodo acaoPeriodo;

    @Column(name = "notificacaoEnviada")
    NotificacaoEnviada notificacaoEnviada;
    @Column(name = "dt_hr_notificacao")
    LocalDateTime dataHoraNotificacao;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "cd_estacionamento", referencedColumnName = "cd_estacionamento", nullable = true)
    private Estacionamento estacionamento;

    @Override
    public String toString() {
        return "Periodo{" +
                "estacionamento='" + this.getEstacionamento().getId() + '\'' +
                ", dataInicial='" + this.getDataHoraInicial() + '\'' +
                ", dataFinal='" + this.getDataHoraFinal() + '\'' +
                ", AcaoPeriodo='" + this.getAcaoPeriodo() + '\'' +
                ", getDataHoraNotificacao='" + this.getDataHoraNotificacao() + '\'' +
                '}';
    }
}
