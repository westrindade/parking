package com.fiap.parking.domain.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface PeriodoService {
    public void save(UUID parquimetro_id);
}
