package com.fiap.parking.domain.service;

import com.fiap.parking.domain.dto.PeriodoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface PeriodoService {
    public void save(UUID estacionamento_id);
}
