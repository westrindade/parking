package com.fiap.parking.domain.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fiap.parking.domain.model.Condutor;
import com.fiap.parking.domain.model.Parquimetro;
import com.fiap.parking.domain.model.Periodo;
import com.fiap.parking.domain.model.StatusParquimetro;
import com.fiap.parking.domain.model.TipoParquimetro;
import com.fiap.parking.domain.model.Veiculo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ParquimetroFixoDTO extends ParquimetroDTO {
    
    public ParquimetroFixoDTO(UUID id, @NotNull String veiculo, @NotNull String condutor, String longitude,
            String latitude, BigDecimal valorHora, BigDecimal valorTotal, StatusParquimetro status,
            List<PeriodoDTO> periodos) {
        super(id, veiculo, condutor, longitude, latitude, valorHora, valorTotal, status, periodos, TipoParquimetro.FIXO);
    }

    public Parquimetro toParquimetro() {
        Parquimetro parquimetro = new Parquimetro(
            TipoParquimetro.FIXO,
            getLatitude(),
            getLongitude(),
            getValorHora(),
            getValorTotal(),
            getStatus()
        );
        parquimetro.setCondutor(Condutor.builder().cpf(condutor).build());
        parquimetro.setVeiculo(Veiculo.builder().placa(veiculo).build());
        final List<Periodo> collect = periodos.stream().map(PeriodoDTO::toPeriodo).collect(Collectors.toList());
        parquimetro.setPeriodos(collect);
        parquimetro.getPeriodos().forEach(p -> p.setParquimetro(parquimetro));
        return parquimetro;
    }
   
    
    public void setPeriodos(List<PeriodoDTO> periodos) {
        this.periodos = periodos;
    }

    @NotNull @Size(min = 1)
    public List<PeriodoDTO> getPeriodo(){
        return this.periodos;
    }
}
