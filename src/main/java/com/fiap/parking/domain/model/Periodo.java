package com.fiap.parking.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_parquimetro_periodo")
public class Periodo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="cd_parquimetro_periodo", unique = true)
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
    @JoinColumn(name = "cd_parquimetro", referencedColumnName = "cd_parquimetro", nullable = true)
    private Parquimetro parquimetro;

}
