package com.fiap.parking.domain.service;

import com.fiap.parking.domain.model.Parquimetro;
import com.fiap.parking.domain.model.Periodo;
import com.fiap.parking.domain.service.PeriodoUtilService;
import com.fiap.parking.infra.utils.Utils;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class PeriodoUtilService {

    public Periodo adicionaPeriodoVariavel(final LocalDateTime dataUltimoPeriodo, final Parquimetro parquimetro) {
        if(dataUltimoPeriodo == null){
            throw new IllegalArgumentException(Utils.getMessage("parametro.data.ultimo.periodo.obrigatorio"));
        }

        if(parquimetro == null){
            throw new IllegalArgumentException(Utils.getMessage("parametro.parquimetro.obrigatorio"));
        }

        LocalDateTime dataInicial = dataUltimoPeriodo.plusSeconds(1);
        Periodo periodo = new Periodo();
        periodo.setParquimetro(parquimetro);
        periodo.setDataHoraInicial(dataInicial);
        periodo.setDataHoraFinal(dataInicial.plusHours(1));

        return periodo;
    }

    public Optional<Periodo> getDataFinalMaisRecenteDaListaDePeriodos(final List<Periodo> listaDePeriodos) {
        if(listaDePeriodos == null){
            throw new IllegalArgumentException(Utils.getMessage("parametro.lista.periodos.obrigatorio"));
        }
        
        final Stream<Periodo> sorted = listaDePeriodos.stream().sorted(Comparator.comparing(Periodo::getDataHoraFinal).reversed());
		return sorted.findFirst();
    }

    public BigDecimal calcularIntervaloHoras(LocalDateTime dataInicio, LocalDateTime dataFim){
        if(dataInicio == null){
            throw new IllegalArgumentException(Utils.getMessage("parametro.data.inicio.obrigatorio"));
        }

        if(dataFim == null){
            throw new IllegalArgumentException(Utils.getMessage("parametro.data.final.obrigatorio"));
        }
       
        final long hours = Duration.between(dataInicio, dataFim).toHours();
        return BigDecimal.valueOf(hours == 0 ? 1 : hours) ;
    }

    public long calcularIntervaloMinutos(LocalDateTime dataInicio, LocalDateTime dataFim){
        if(dataInicio == null){
            throw new IllegalArgumentException(Utils.getMessage("parametro.data.inicio.obrigatorio"));
        }

        if(dataFim == null){
            throw new IllegalArgumentException(Utils.getMessage("parametro.data.final.obrigatorio"));
        }

        return Duration.between(dataInicio, dataFim).toMinutes();
    }
}
