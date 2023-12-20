package com.fiap.parking.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
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
}
