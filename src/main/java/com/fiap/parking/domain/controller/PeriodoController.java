package com.fiap.parking.domain.controller;

import com.fiap.parking.domain.dto.EstacionamentoDTO;
import com.fiap.parking.domain.dto.PeriodoDTO;
import com.fiap.parking.domain.service.PeriodoService;
import com.fiap.parking.domain.service.PeriodoUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/periodo")
public class PeriodoController {
    @Autowired
    private PeriodoService periodoService;
    @PostMapping("/{estacionamento_id}")
    public void save(@PathVariable UUID estacionamento_id){
        this.periodoService.save(estacionamento_id);
    }
}
