package com.fiap.parking.domain.service;

import com.fiap.parking.domain.dto.ParquimetroDTO;
import com.fiap.parking.domain.model.TipoParquimetro;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface ParquimetroService {

    public List<ParquimetroDTO> findAll();
    public ParquimetroDTO findById(UUID id);
    public List<ParquimetroDTO> findByStatus(String status);

    public List<ParquimetroDTO> findByStatusAndTipoParquimetro(String status, String tipoParquimetro);

    public ParquimetroDTO save(ParquimetroDTO parquimetroDTO, TipoParquimetro tipoParquimetro);
    public ParquimetroDTO condutorInformaResposta(UUID id);

}
