package com.fiap.parking.domain.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.fiap.parking.domain.model.Condutor;
import com.fiap.parking.domain.model.Parquimetro;
import com.fiap.parking.domain.model.Periodo;
import com.fiap.parking.domain.model.StatusParquimetro;
import com.fiap.parking.domain.model.TipoParquimetro;
import com.fiap.parking.domain.model.Veiculo;

import jakarta.validation.constraints.NotNull;


public class ParquimetroVariavelDTO extends ParquimetroDTO {
    
    public ParquimetroVariavelDTO(UUID id, @NotNull String veiculo, @NotNull String condutor, String longitude,
            String latitude, BigDecimal valorHora, BigDecimal valorTotal, StatusParquimetro status,
            List<Periodo> periodos) {
        super(id, veiculo, condutor, longitude, latitude, valorHora, valorTotal, status, periodos);
    }

    public Parquimetro toParquimetro() {
            Parquimetro parquimetro = new Parquimetro(
            TipoParquimetro.VARIAVEL,
            getLatitude(),
            getLongitude(),
            getValorHora(),
            getValorTotal(),
            getStatus()
    );
        parquimetro.setCondutor(Condutor.builder().cpf(condutor).build());
        parquimetro.setVeiculo(Veiculo.builder().placa(veiculo).build());
        if(periodos != null){
            parquimetro.setPeriodos(periodos);
            periodos.forEach(p -> p.setParquimetro(parquimetro));
        }
        return parquimetro;
    }

    public void setPeriodos(List<Periodo> periodos) {
        this.periodos = periodos;
    }
}