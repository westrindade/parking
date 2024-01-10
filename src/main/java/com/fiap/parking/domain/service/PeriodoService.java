package com.fiap.parking.domain.service;

import com.fiap.parking.domain.exception.EntidadeNaoEncontrada;
import com.fiap.parking.domain.model.Parquimetro;
import com.fiap.parking.domain.model.Periodo;
import com.fiap.parking.domain.repositories.ParquimetroRepository;
import com.fiap.parking.domain.repositories.PeriodoRepository;
import com.fiap.parking.domain.service.PeriodoService;
import com.fiap.parking.infra.utils.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PeriodoService {
    @Autowired
    private PeriodoRepository periodoRepository;
    @Autowired
    private ParquimetroService parquimetroService;
    @Autowired
    private PeriodoUtilService periodoUtilService;

    public void save(UUID parquimetro_id) {
        final Parquimetro parquimetro = this.parquimetroService.findById(parquimetro_id).toParquimetro();
        final Optional<Periodo> ultimoPeriodo = this.periodoUtilService.getDataFinalMaisRecenteDaListaDePeriodos(parquimetro.getPeriodos());
        this.periodoRepository.save(this.periodoUtilService.adicionaPeriodoVariavel(ultimoPeriodo.get().getDataHoraFinal(),parquimetro));
    }
}
