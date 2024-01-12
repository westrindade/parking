package com.fiap.parking.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fiap.parking.domain.model.Veiculo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Representa o veiculo", title = "Veiculo")
public record VeiculoDTO(
        @NotBlank String placa,
        @NotBlank String modelo,
        @NotBlank String cor
){
    public Veiculo toVeiculo() {
        return new Veiculo(placa, modelo, cor);
    }
}
