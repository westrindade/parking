package com.fiap.parking.domain.service;

import com.fiap.parking.domain.exception.EntidadeNaoEncontrada;
import com.fiap.parking.domain.model.Parquimetro;
import com.fiap.parking.domain.model.Periodo;
import com.fiap.parking.domain.repositories.ParquimetroRepository;
import com.fiap.parking.domain.repositories.PeriodoRepository;
import com.fiap.parking.domain.service.PeriodoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PeriodoService {
    @Autowired
    private PeriodoRepository periodoRepository;

    @Autowired
    private ParquimetroRepository parquimetroRepository;

    @Autowired
    private PeriodoUtilService periodoUtilService;

    public void save(UUID parquimetro_id) {
        Parquimetro parquimetro = this.parquimetroRepository.findById(parquimetro_id)
                .orElseThrow(() -> new EntidadeNaoEncontrada("Parquimetro não existe"));

        Optional<Periodo> ultimoPeriodo = this.periodoUtilService.ordenarDecrescentePegarPrimeiro(parquimetro.getPeriodos());

        this.periodoRepository.save(this.periodoUtilService.adicionaPeriodoVariavel(ultimoPeriodo.get().getDataHoraFinal(),parquimetro));
    }
}
