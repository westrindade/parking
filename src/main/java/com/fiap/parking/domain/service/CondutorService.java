package com.fiap.parking.domain.service;

import com.fiap.parking.domain.dto.CondutorDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CondutorService {
    public ResponseEntity<?> findAll();

    public ResponseEntity<?> findByCpf(String cpf);

    public ResponseEntity<?> save(CondutorDTO condutorDTO);

    public ResponseEntity<?> savePayment(String cpf, String tipoPagamento);
}
