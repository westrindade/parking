package com.fiap.parking.domain.service;

import com.fiap.parking.domain.model.Condutor;
import com.fiap.parking.domain.model.TipoPagamento;
import com.fiap.parking.domain.repositories.CondutorRepository;

import com.fiap.parking.domain.dto.CondutorDTO;
import com.fiap.parking.domain.exception.EntidadeNaoEncontrada;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
                .orElseThrow( () -> new EntidadeNaoEncontrada("excecao.condutor.nao.encontrado") );
        return condutor.toDTO();
    }

    public CondutorDTO save(CondutorDTO condutorDTO) {
        final Condutor condutor = condutorDTO.toCondutor();
        return this.condutorRepository.save(condutor).toDTO();
    }

    public void savePayment(final String cpf, TipoPagamento tipoPagamento) {
        final var condutor =  this.condutorRepository.findById(cpf).orElseThrow( () -> new EntidadeNaoEncontrada("excecao.condutor.nao.encontrado"));

        condutor.setTipoPagamentoPadrao(tipoPagamento);
        this.condutorRepository.save(condutor);
    }
}