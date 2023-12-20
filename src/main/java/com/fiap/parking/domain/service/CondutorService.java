package com.fiap.parking.domain.service;

import com.fiap.parking.domain.dto.CondutorDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CondutorService {
    public List<CondutorDTO> findAll();

    public CondutorDTO findByCpf(String cpf);

    public CondutorDTO  save(CondutorDTO condutorDTO);

    public void savePayment(String cpf, String tipoPagamento);
}
