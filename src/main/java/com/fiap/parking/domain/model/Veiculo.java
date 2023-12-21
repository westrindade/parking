package com.fiap.parking.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_veiculo")
public class Veiculo {

    @Id
    @Column(name = "placa", unique = true)
    private String placa;

    @Column(name = "modelo")
    private String modelo;

    @Column(name = "cor")
    private String cor;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "condutor_cpf", referencedColumnName = "cpf", nullable = false)
    private Condutor condutor;
}
