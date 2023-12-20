package com.fiap.parking.domain.dto;

import com.fiap.parking.domain.model.TipoPagamento;
import com.fiap.parking.domain.model.Veiculo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record CondutorDTO (
        String cpf,
        @NotBlank(message = "O nome precisa ser informado")
        String nome,
        @NotNull(message = "O celular precisa ser informado")
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
        @Size(message = "Nao pode estar vazia", min = 1)
        List<Veiculo> veiculos
) {}
