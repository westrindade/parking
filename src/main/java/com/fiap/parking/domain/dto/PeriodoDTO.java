package com.fiap.parking.domain.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PeriodoDTO(
        UUID estacionamento,
        LocalDateTime dataHoraInicial,
        LocalDateTime dataHoraFinal
) {
}
