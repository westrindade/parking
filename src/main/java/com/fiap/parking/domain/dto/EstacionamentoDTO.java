package com.fiap.parking.domain.dto;

import com.fiap.parking.domain.model.Periodo;
import com.fiap.parking.domain.model.StatusEstacionamento;
import com.fiap.parking.domain.model.TipoTempo;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record EstacionamentoDTO(
        UUID idEstacionamento,
        TipoTempo tipoTempo,
        String veiculo,
        String condutor,
        String longitude,
        String latitude,
        BigDecimal valorHora,
        BigDecimal valorTotal,
        StatusEstacionamento status,
        List<Periodo> periodos
) {
}
