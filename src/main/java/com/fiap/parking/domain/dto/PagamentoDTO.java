package com.fiap.parking.domain.dto;

import com.fiap.parking.domain.model.Parquimetro;
import com.fiap.parking.domain.model.StatusPagamento;
import com.fiap.parking.domain.model.TipoPagamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PagamentoDTO(
        UUID id,
        StatusPagamento status,
        LocalDateTime dataHora,
        @NotBlank(message = "O tipo de pagamento precisa ser informado")
        TipoPagamento tipoPagamento,
        BigDecimal valor,
        Parquimetro parquimetro
) {
}
