package com.fiap.parking.domain.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fiap.parking.domain.model.Periodo;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PeriodoDTO(
    UUID parquimetro,
    @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss") LocalDateTime dataHoraInicial,
    @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss") LocalDateTime dataHoraFinal
) {
    public Periodo toPeriodo(){
        return new Periodo(parquimetro, dataHoraInicial, dataHoraFinal);
    }
}
