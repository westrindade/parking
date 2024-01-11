package com.fiap.parking.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fiap.parking.domain.dto.PagamentoDTO;

@Data
@NoArgsConstructor
@Entity
@Table(name = "tb_pagamento")
public class Pagamento {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="cd_pagamento", unique = true)
    private UUID id;

    @Column(name = "status")
    private StatusPagamento status;

    @Column(name = "dt_hr")
    LocalDateTime dataHora;

    @Column(name = "tp_pagamento", nullable = false)
    private TipoPagamento tipoPagamento;

    @Column(name = "valor")
    private BigDecimal valor;

    @ManyToOne
    @JoinColumn(name = "cd_parquimetro", referencedColumnName = "cd_parquimetro")
    private Parquimetro parquimetro;

    public Pagamento(Parquimetro parquimetro){
        this.tipoPagamento = parquimetro.getCondutor().getTipoPagamentoPadrao();
        this.status = StatusPagamento.SUCESSO;
        this.parquimetro = parquimetro;
        this.valor = parquimetro.getValorTotal();
        this.dataHora = LocalDateTime.now();
    }

    public PagamentoDTO toDTO() {
        return new PagamentoDTO(
                this.getId(),
                this.getStatus(),
                this.getDataHora(),
                this.getTipoPagamento(),
                this.getValor(),
                this.getParquimetro()
        );
    }
}
