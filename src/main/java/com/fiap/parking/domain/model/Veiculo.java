package com.fiap.parking.domain.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiap.parking.domain.dto.VeiculoDTO;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_veiculo")
public class Veiculo {

    @NotBlank
    @Id
    @Column(name = "placa", unique = true)
    private String placa;

    @NotBlank
    @Column(name = "modelo")
    private String modelo;

    @NotBlank
    @Column(name = "cor")
    private String cor;

    @JsonIgnore
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "condutor_cpf", referencedColumnName = "cpf", nullable = false)
    private Condutor condutor;

    public VeiculoDTO toDTO(){
        return new VeiculoDTO(
                this.getPlaca(),
                this.getModelo(),
                this.getCor()
        );
    }

    public Veiculo(@NotBlank String placa, @NotBlank String modelo, @NotBlank String cor) {
        this.placa = placa;
        this.modelo = modelo;
        this.cor = cor;
    }

}
