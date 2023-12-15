package com.fiap.parking.domain.service.impl;

import com.fiap.parking.domain.model.Parquimetro;
import com.fiap.parking.domain.model.Periodo;
import com.fiap.parking.domain.service.PeriodoUtilService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class PeriodoUtilServiceImpl implements PeriodoUtilService {
    @Override
    public Periodo adicionaPeriodoVariavel(LocalDateTime dataUltimoPeriodo, Parquimetro parquimetro) {
        LocalDateTime dataInicial = dataUltimoPeriodo.plusSeconds(1);
        Periodo periodo = new Periodo();
        periodo.setParquimetro(parquimetro);
        periodo.setDataHoraInicial(dataInicial);
        periodo.setDataHoraFinal(dataInicial.plusHours(1));

        return periodo;
    }

    public Optional<Periodo> ordenarDecrescentePegarPrimeiro(List<Periodo> listaDePeriodos) {
        return listaDePeriodos.stream()
                .sorted(Comparator.comparing(Periodo::getDataHoraFinal).reversed())
                .findFirst();
    }

    public long calcularIntervaloHoras(LocalDateTime dataInicio, LocalDateTime dataFim){
        Duration duracao = Duration.between(dataInicio, dataFim);
        return duracao.toHours() == 0 ? 1 : duracao.toHours() ;
    }

    public long calcularIntervaloMinutos(LocalDateTime dataInicio, LocalDateTime dataFim){
        Duration duracao = Duration.between(dataInicio, dataFim);
        return duracao.toMinutesPart();
    }
}
