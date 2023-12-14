package com.fiap.parking.domain.service;

import com.fiap.parking.domain.dto.VeiculoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface VeiculoService {
    public ResponseEntity<?> findByCondutorCpf(String cpf);

    public ResponseEntity<?> findById(String placa);
}
