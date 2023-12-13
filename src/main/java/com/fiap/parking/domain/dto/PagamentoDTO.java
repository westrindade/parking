package com.fiap.parking.domain.dto;

import com.fiap.parking.domain.model.Estacionamento;
import com.fiap.parking.domain.model.StatusPagamento;
import com.fiap.parking.domain.model.TipoPagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PagamentoDTO(
        UUID id,
        StatusPagamento status,
        LocalDateTime dataHora,
        TipoPagamento tipoPagamento,
        BigDecimal valor,
        Estacionamento estacionamento
) {
}
