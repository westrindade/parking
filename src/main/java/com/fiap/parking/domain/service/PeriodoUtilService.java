package com.fiap.parking.domain.service;

import com.fiap.parking.domain.model.Estacionamento;
import com.fiap.parking.domain.model.Periodo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public interface PeriodoUtilService {

    public Periodo addHoraPeriodo(LocalDateTime dataInicial, Estacionamento estacionamento);

}