package com.fiap.parking.domain.service;

import com.fiap.parking.domain.dto.CondutorDTO;
import com.fiap.parking.domain.dto.EstacionamentoDTO;
import com.fiap.parking.domain.dto.VeiculoDTO;
import com.fiap.parking.domain.model.Estacionamento;
import com.fiap.parking.domain.model.TipoTempo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface EstacionamentoService {

    public ResponseEntity<?> findAll();
    public ResponseEntity<?> findById(UUID id);
    public ResponseEntity<?> findByStatus(String status);

    public ResponseEntity<?> findByStatusAndTipoTempo(String status,String tipoTempo);

    public ResponseEntity<?> save(EstacionamentoDTO estacionamentoDTO, TipoTempo tipoTempo);
    public ResponseEntity<?> condutorInformaResposta(UUID id);

}
