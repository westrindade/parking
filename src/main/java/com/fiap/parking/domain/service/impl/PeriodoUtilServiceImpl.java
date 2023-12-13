package com.fiap.parking.domain.service.impl;

import com.fiap.parking.domain.model.Estacionamento;
import com.fiap.parking.domain.model.Periodo;
import com.fiap.parking.domain.service.PeriodoUtilService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PeriodoUtilServiceImpl implements PeriodoUtilService {
    @Override
    public Periodo addHoraPeriodo(LocalDateTime dataInicial, Estacionamento estacionamento) {
        Periodo periodo = new Periodo();
        periodo.setEstacionamento(estacionamento);
        periodo.setDataHoraInicial(dataInicial);
        periodo.setDataHoraFinal(dataInicial.plusHours(1));

        return periodo;
    }
}
