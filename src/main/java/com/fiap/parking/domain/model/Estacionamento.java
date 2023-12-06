package com.fiap.parking.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "tb_estacionamento")
public class Estacionamento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="cd_estacionamento", unique = true)
    private UUID idEstacionamento;

    @ManyToOne
    @JoinColumn(name = "condutor_cpf")
    private Condutor condutor;

    @ManyToOne
    @JoinColumn(name = "veiculo_placa")
    private Veiculo veiculo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_tempo", nullable = true)
    private TipoTempo tipoTempo;

    //para estacionamento variavel
    /*List<Periodo>periodos = new ArrayList<>();*/
}
