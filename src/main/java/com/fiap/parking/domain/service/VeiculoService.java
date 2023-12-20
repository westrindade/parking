package com.fiap.parking.domain.service;

import com.fiap.parking.domain.dto.VeiculoDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface VeiculoService {
    public List<VeiculoDTO> findByCondutorCpf(String cpf);

    public VeiculoDTO  findById(String placa);
}
