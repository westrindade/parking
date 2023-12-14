package com.fiap.parking.domain.service.impl;

import com.fiap.parking.domain.dto.VeiculoDTO;
import com.fiap.parking.domain.model.Veiculo;
import com.fiap.parking.domain.repositories.VeiculoRepository;
import com.fiap.parking.domain.service.VeiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VeiculoServiceImpl implements VeiculoService {

    @Autowired
    private VeiculoRepository veiculoRepository;
    @Override
    public ResponseEntity<?> findByCondutorCpf(String cpf) {

        try {
            var veiculo = this.veiculoRepository.findByCondutorCpf(cpf);
            return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(
                    veiculo.stream().map(this::toVeiculoDTO).collect(Collectors.toList())
            );
        } catch (IllegalArgumentException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> findById(String placa) {
        try {
            var veiculo = this.toVeiculoDTO(this.veiculoRepository.findById(placa)
                    .orElseThrow( () -> new IllegalArgumentException("Veiculo não encontrado") ));

            return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(veiculo);
        } catch (IllegalArgumentException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    private VeiculoDTO toVeiculoDTO(Veiculo veiculo){
        return new VeiculoDTO(
                veiculo.getPlaca(),
                veiculo.getModelo(),
                veiculo.getCor()
        );
    }
}
