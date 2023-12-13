package com.fiap.parking.domain.service;

import com.fiap.parking.domain.model.Estacionamento;
import com.fiap.parking.domain.model.Periodo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public interface PeriodoUtilService {

    public Periodo addHoraPeriodo(LocalDateTime dataInicial, Estacionamento estacionamento);
    public Optional<Periodo> ordenarDecrescentePegarPrimeiro(List<Periodo> listaDePeriodos);
    public long calcularIntervaloHoras(LocalDateTime dataInicio, LocalDateTime dataFim);
    public long calcularIntervaloMinutos(LocalDateTime dataInicio, LocalDateTime dataFim);
}