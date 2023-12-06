package com.fiap.parking.domain.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Id;

public record VeiculoDTO (
        String placa,
        String modelo,
        String cor
) { }
