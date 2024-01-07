package com.fiap.parking.domain.dto;

import com.fiap.parking.domain.model.Veiculo;

import jakarta.validation.constraints.NotBlank;

public record VeiculoDTO(
        @NotBlank String placa,
        @NotBlank String modelo,
        @NotBlank String cor
){
    public Veiculo toVeiculo() {
        return new Veiculo(placa, modelo, cor);
    }
}
