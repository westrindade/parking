package com.fiap.parking.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fiap.parking.domain.model.StatusPagamento;
import com.fiap.parking.domain.model.TipoPagamento;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PagamentoDTO(
    UUID id,
    StatusPagamento status,
    @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss") LocalDateTime dataHora,
    @NotBlank(message = "O tipo de pagamento precisa ser informado")
    TipoPagamento tipoPagamento,
    BigDecimal valor,
    ParquimetroDTO parquimetro
) {}
