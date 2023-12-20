package com.fiap.parking.domain.dto;

import com.fiap.parking.domain.model.Periodo;
import com.fiap.parking.domain.model.StatusParquimetro;
import com.fiap.parking.domain.model.TipoParquimetro;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ParquimetroDTO(
        UUID id,
        @NotNull(message = "O tipo de tempo precisa ser informado")
        TipoParquimetro tipoParquimetro,
        String veiculo,
        String condutor,
        String longitude,
        String latitude,
        BigDecimal valorHora,
        BigDecimal valorTotal,
        StatusParquimetro status,
        List<Periodo> periodos
) {}
