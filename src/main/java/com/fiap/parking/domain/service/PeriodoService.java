package com.fiap.parking.domain.service;

import com.fiap.parking.domain.model.Periodo;
import com.fiap.parking.domain.repositories.PeriodoRepository;
import com.fiap.parking.domain.service.PeriodoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PeriodoService {
    @Autowired
    private PeriodoRepository periodoRepository;

    public void save(Periodo periodo) {
        this.periodoRepository.save(periodo);
    }
   
}
