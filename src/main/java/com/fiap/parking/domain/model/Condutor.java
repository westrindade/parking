package com.fiap.parking.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import org.hibernate.validator.constraints.br.CPF;
import org.springframework.format.annotation.DateTimeFormat;

import com.fiap.parking.domain.dto.CondutorDTO;
import com.fiap.parking.domain.dto.VeiculoDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "tb_condutor")
public class Condutor {

    @NotBlank @CPF
    @Id
    @Column(name = "cpf", unique = true)
    private String cpf;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "celular")
    private String celular;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "dt_nascimento")
    private LocalDate dataNascimento;
    @Column(name = "tp_logradouro")
    private String tipoLogradouro;
    @Column(name = "logradouro")
    private String logradouro;
    @Column(name = "nro_logradouro")
    private String nroLogradouro;
    @Column(name = "bairro")
    private String bairro;
    @Column(name = "cidade")
    private String cidade;
    @Column(name = "uf")
    private String uf;
    @Column(name = "cep")
    private String cep;
    @Enumerated(EnumType.STRING)
   
    @NotNull
    @Column(name = "tp_pagamento", nullable = false)
    private TipoPagamento tipoPagamentoPadrao;

    @NotNull @Size(min = 1)
    @OneToMany(mappedBy = "condutor", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Veiculo> veiculos = new ArrayList<>();
    
    public Condutor(){}

    public Condutor(
        @NotBlank @CPF String cpf, 
        @NotBlank String nome, 
        String celular, 
        LocalDate dataNascimento, 
        String tipoLogradouro,
        String logradouro, String nroLogradouro, String bairro, String cidade, String uf, String cep,
        @NotNull TipoPagamento tipoPagamento
    ) {
        this.cpf = cpf;
        this.nome = nome;
        this.celular = celular;
        this.dataNascimento = dataNascimento;
        this.tipoLogradouro = tipoLogradouro;
        this.logradouro = logradouro;
        this.nroLogradouro = nroLogradouro;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
        this.cep = cep;
        this.tipoPagamentoPadrao = tipoPagamento;
    }

    @PrePersist
    public void persist(){
    	if (veiculos == null) return;

        for (Veiculo veiculo : veiculos) {
            veiculo.setCondutor(this);
        }
    }

    public CondutorDTO toDTO() {
        final List<VeiculoDTO> veiculosDTO = this.getVeiculos().stream().map(Veiculo::toDTO).collect(Collectors.toList());

        return new CondutorDTO(
            this.getCpf(),
            this.getNome(),
            this.getCelular(),
            this.getDataNascimento(),
            this.getTipoLogradouro(),
            this.getLogradouro(),
            this.getNroLogradouro(),
            this.getBairro(),
            this.getCidade(),
            this.getUf(),
            this.getCep(),
            this.getTipoPagamentoPadrao(),
            veiculosDTO
        );
    }

}
