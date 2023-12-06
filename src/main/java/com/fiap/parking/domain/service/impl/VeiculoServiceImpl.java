package com.fiap.parking.domain.service.impl;

import com.fiap.parking.domain.dto.VeiculoDTO;
import com.fiap.parking.domain.model.Veiculo;
import com.fiap.parking.domain.repositories.VeiculoRepository;
import com.fiap.parking.domain.service.VeiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VeiculoServiceImpl implements VeiculoService {

    @Autowired
    private VeiculoRepository veiculoRepository;
    @Override
    public List<VeiculoDTO> findByCondutorCpf(String cpf) {
        var veiculo = this.veiculoRepository.findByCondutorCpf(cpf);
                //.orElseThrow( () -> new IllegalArgumentException("Não encontrado veiculo") ));

        return veiculo.stream().map(this::toVeiculoDTO).collect(Collectors.toList());
    }

    @Override
    public VeiculoDTO findById(String placa) {
        var veiculo = this.toVeiculoDTO(this.veiculoRepository.findById(placa)
                .orElseThrow( () -> new IllegalArgumentException("Veiculo não encontrado") ));

        return veiculo;
    }

    private VeiculoDTO toVeiculoDTO(Veiculo veiculo){
        return new VeiculoDTO(
                veiculo.getPlaca(),
                veiculo.getModelo(),
                veiculo.getCor()
        );
    }
}
