package com.fiap.parking.domain.service;

import com.fiap.parking.domain.model.Condutor;
import com.fiap.parking.domain.model.TipoPagamento;
import com.fiap.parking.domain.model.Veiculo;
import com.fiap.parking.domain.repositories.CondutorRepository;
import com.fiap.parking.domain.dto.CondutorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CondutorService {

    @Autowired
    private CondutorRepository condutorRepository;

    public List<CondutorDTO> findAll() {
        var condutor = this.condutorRepository.findAll();

        return condutor.stream().map(Condutor::toDTO).collect(Collectors.toList());
    }

    public CondutorDTO findByCpf(String cpf) {
        var condutor =  this.condutorRepository.findById(cpf)
                .orElseThrow( () -> new IllegalArgumentException("Condutor não encontrado") );
        return condutor.toDTO();
    }

    public CondutorDTO save(CondutorDTO condutorDTO) {
        final Condutor condutor = condutorDTO.toCondutor();

        List<Veiculo> veiculos = new ArrayList<>();
        for (Veiculo veiculo : condutorDTO.veiculos()) {
            veiculo.setCondutor(condutor);
            veiculos.add(veiculo);
        }
        condutor.setVeiculos(veiculos);

        return this.condutorRepository.save(condutor).toDTO();
    }

    public void savePayment(String cpf, String tipoPagamento) {
        String tipoPagamentoUpperCase = tipoPagamento.toUpperCase();
        if (Arrays.stream(TipoPagamento.values())
                .noneMatch(enumValue -> enumValue.name().equals(tipoPagamentoUpperCase))) {
            throw new IllegalArgumentException("Tipo de pagamento inválido: " + tipoPagamento);
        }
        var condutor =  this.condutorRepository.findById(cpf)
                .orElseThrow( () -> new IllegalArgumentException("Condutor não encontrado") );

        condutor.setTipoPagamentoPadrao(TipoPagamento.valueOf(tipoPagamento.toUpperCase()));
        this.condutorRepository.save(condutor);
    }
}