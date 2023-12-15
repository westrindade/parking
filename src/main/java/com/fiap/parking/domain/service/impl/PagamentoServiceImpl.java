package com.fiap.parking.domain.service.impl;

import com.fiap.parking.domain.dto.PagamentoDTO;
import com.fiap.parking.domain.model.Parquimetro;
import com.fiap.parking.domain.model.Pagamento;
import com.fiap.parking.domain.model.StatusPagamento;
import com.fiap.parking.domain.model.TipoPagamento;
import com.fiap.parking.domain.repositories.ParquimetroRepository;
import com.fiap.parking.domain.repositories.PagamentoRepository;
import com.fiap.parking.domain.service.PagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PagamentoServiceImpl implements PagamentoService {

    @Autowired
    private ParquimetroRepository parquimetroRepository;
    @Autowired
    private PagamentoRepository pagamentoRepository;
    @Override
    public PagamentoDTO pagamento(UUID parquimetro_id) {

        Parquimetro parquimetro = this.parquimetroRepository.findById(parquimetro_id)
                .orElseThrow(()-> new IllegalArgumentException("Parquimetro nao encontrado"));

        this.tipoParquimetroVariavel(parquimetro);

        Pagamento pagamento = new Pagamento();
        pagamento.setTipoPagamento(parquimetro.getCondutor().getTipoPagamentoPadrao());
        pagamento.setStatus(StatusPagamento.SUCESSO);
        pagamento.setParquimetro(parquimetro);
        pagamento.setValor(parquimetro.getValorTotal());
        pagamento.setDataHora(LocalDateTime.now());

        pagamento = this.pagamentoRepository.save(pagamento);

        return this.toPagamentoDTO(pagamento);

    }

    private void tipoParquimetroVariavel(Parquimetro parquimetro){
        if (parquimetro.getCondutor().getTipoPagamentoPadrao() == TipoPagamento.PIX){
            throw new IllegalStateException("PIX não é aceito em parquimetro VARIAVEL");
        }
    }

    private PagamentoDTO toPagamentoDTO(Pagamento pagamento) {
        return new PagamentoDTO(
                pagamento.getId(),
                pagamento.getStatus(),
                pagamento.getDataHora(),
                pagamento.getTipoPagamento(),
                pagamento.getValor(),
                pagamento.getParquimetro()
        );
    }
}
