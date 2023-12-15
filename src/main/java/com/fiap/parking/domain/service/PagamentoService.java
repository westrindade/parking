package com.fiap.parking.domain.service;

import com.fiap.parking.domain.dto.PagamentoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
public interface PagamentoService {
    public PagamentoDTO  pagamento(UUID idEstacionamento);
}
