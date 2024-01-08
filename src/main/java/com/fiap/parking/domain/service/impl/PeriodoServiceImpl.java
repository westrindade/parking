package com.fiap.parking.domain.service.impl;

import com.fiap.parking.domain.model.Parquimetro;
import com.fiap.parking.domain.model.Periodo;
import com.fiap.parking.domain.repositories.ParquimetroRepository;
import com.fiap.parking.domain.repositories.PeriodoRepository;
import com.fiap.parking.domain.service.PeriodoService;
import com.fiap.parking.domain.service.PeriodoUtilService;
import com.fiap.parking.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PeriodoServiceImpl implements PeriodoService {
    @Autowired
    private PeriodoRepository periodoRepository;
    @Autowired
    private ParquimetroRepository parquimetroRepository;
    @Autowired
    private PeriodoUtilService periodoUtilService;
    @Override
    public void save(UUID parquimetro_id) {

        Parquimetro parquimetro = this.parquimetroRepository.findById(parquimetro_id)
                .orElseThrow(() -> new IllegalArgumentException(Utils.getMessage("parquimetro.nao.encontrado")));

        Optional<Periodo> ultimoPeriodo = this.periodoUtilService.ordenarDecrescentePegarPrimeiro(parquimetro.getPeriodos());

        this.periodoRepository.save(this.periodoUtilService.adicionaPeriodoVariavel(ultimoPeriodo.get().getDataHoraFinal(),parquimetro));

    }

}
