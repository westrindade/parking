package com.fiap.parking.domain.service;

import com.fiap.parking.domain.dto.CondutorDTO;
import com.fiap.parking.domain.dto.EstacionamentoDTO;
import com.fiap.parking.domain.dto.VeiculoDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface EstacionamentoService {

    public List<EstacionamentoDTO> findAll();
    public EstacionamentoDTO findById(UUID id);
    public List<EstacionamentoDTO> findByStatus(String status);

    public List<EstacionamentoDTO> findByStatusAndTipoTempo(String status,String tipoTempo);

    public EstacionamentoDTO saveFixo(EstacionamentoDTO estacionamentoDTO);
    public EstacionamentoDTO saveVariavel(EstacionamentoDTO estacionamentoDTO);
}
