package com.fiap.parking.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tb_condutor")
public class Condutor {

    @Id
    @Column(name = "cpf", unique = true)
    private String cpf;

    @Column(name = "nome")
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
    @Column(name = "tp_pagamento", nullable = true)
    private TipoPagamento tipoPagamentoPadrao;

    @OneToMany(mappedBy = "condutor", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Veiculo> veiculos = new ArrayList<>();

    //private transient List<Veiculo> testeVeiculo;
    
    public Condutor(){}
    
    public Condutor(String cpf){}


    public Condutor(String cpf, String nome, String celular, LocalDate dataNascimento, String tipoLogradouro,
                    String logradouro, String nroLogradouro, String bairro, String cidade, String uf, String cep,
                    @Size(min = 1)
                    List<Veiculo> veiculos
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
        this.veiculos = veiculos;
    }

//    @PrePersist
//    public void persist(){
//    	if (veiculos == null) return;
//
//        for (Veiculo veiculo : veiculos) {
//            veiculo.setCondutor(this);
//        }
//    }

    @Override
    public String toString() {
        return "Condutor{" +
                "cpf='" + this.cpf + '\'' +
                ", nome='" + this.nome + '\'' +
                // Evite chamar toString() na lista de veículos para evitar recursão infinita
                ", veiculos=" + (this.veiculos != null ? this.veiculos.size() : "null") +
                ",TipoPagamentoPadrao=" + this.tipoPagamentoPadrao +
                '}';
    }

//	public void setTesteVeiculo(List<Veiculo> veiculos2) {
//		this.testeVeiculo = veiculos2;
//	}
}
