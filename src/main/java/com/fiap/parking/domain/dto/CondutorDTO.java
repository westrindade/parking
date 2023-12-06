package com.fiap.parking.domain.dto;

import com.fiap.parking.domain.model.TipoPagamento;
import com.fiap.parking.domain.model.Veiculo;

import java.time.LocalDate;
import java.util.List;

public record CondutorDTO (
        String cpf,
        String nome,
        String celular,
        LocalDate dataNascimento,
        String tipoLogradouro,
        String logradouro,
        String nroLogradouro,
        String bairro,
        String cidade,
        String uf,
        String cep,
        TipoPagamento tipoPagamentoPadrao,
        List<Veiculo> veiculos
) { }
