package com.fiap.parking.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fiap.parking.domain.model.Condutor;
import com.fiap.parking.domain.model.TipoPagamento;
import com.fiap.parking.domain.model.Veiculo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.validator.constraints.br.CPF;

@Schema(description = "Representa o condutor do veiculo", title = "Condutor")
public record CondutorDTO (
    //@CPF 
    @NotBlank(message = "O cpf precisa ser informado") String cpf,
    @NotBlank(message = "O nome precisa ser informado") String nome,
    @NotNull(message = "O celular precisa ser informado") String celular,
    @JsonFormat(pattern="dd/MM/yyyy") LocalDate dataNascimento,
    String tipoLogradouro,
    String logradouro,
    String nroLogradouro,
    String bairro,
    String cidade,
    String uf,
    String cep,
    TipoPagamento tipoPagamentoPadrao,
    @NotNull @Size(min = 1) List<VeiculoDTO> veiculos
) {
    public Condutor toCondutor() {
        final Condutor condutor = new Condutor(
            this.cpf(),
            this.nome(),
            this.celular(),
            this.dataNascimento(),
            this.tipoLogradouro(),
            this.logradouro(),
            this.nroLogradouro(),
            this.bairro(),
            this.cidade(),
            this.uf(),
            this.cep(),
            this.tipoPagamentoPadrao()
        );
        
        final List<Veiculo> veiculosList = veiculos().stream().map(veiculoDTO -> 
            veiculoDTO.toVeiculo().toBuilder()
                .condutor(condutor)
            .build()
        ).collect(Collectors.toList());

        condutor.setVeiculos(veiculosList);
        return condutor;
    }
}
