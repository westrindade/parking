package com.fiap.parking.domain.service;

import com.fiap.parking.domain.dto.VeiculoDTO;
import com.fiap.parking.domain.model.Veiculo;
import com.fiap.parking.domain.repositories.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VeiculoService {

    @Autowired
    private VeiculoRepository veiculoRepository;

    public List<VeiculoDTO> findByCondutorCpf(String cpf) {
        var veiculo = this.veiculoRepository.findByCondutorCpf(cpf);
        return veiculo.stream().map(Veiculo::toDTO).collect(Collectors.toList());
    }

    public VeiculoDTO findById(String placa) {
        var veiculo = this.veiculoRepository.findById(placa)
                .orElseThrow( () -> new IllegalArgumentException("Veiculo não encontrado") );

        return veiculo.toDTO();
    }

    
}
