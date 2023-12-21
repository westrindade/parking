package com.fiap.parking.domain.service;

import com.fiap.parking.domain.dto.PagamentoDTO;
import com.fiap.parking.domain.exception.EntidadeNaoEncontrada;
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
public class PagamentoService {

    @Autowired
    private ParquimetroRepository parquimetroRepository;
    @Autowired
    private PagamentoRepository pagamentoRepository;

    public PagamentoDTO pagamento(UUID parquimetro_id) {
        Parquimetro parquimetro = this.parquimetroRepository.findById(parquimetro_id)
                .orElseThrow(()-> new EntidadeNaoEncontrada("Parquimetro nao encontrado"));

        this.tipoParquimetroVariavel(parquimetro);

        Pagamento pagamento = new Pagamento();
        pagamento.setTipoPagamento(parquimetro.getCondutor().getTipoPagamentoPadrao());
        pagamento.setStatus(StatusPagamento.SUCESSO);
        pagamento.setParquimetro(parquimetro);
        pagamento.setValor(parquimetro.getValorTotal());
        pagamento.setDataHora(LocalDateTime.now());

        pagamento = this.pagamentoRepository.save(pagamento);

        return pagamento.toDTO();
    }

    private void tipoParquimetroVariavel(Parquimetro parquimetro){
        if (parquimetro.getCondutor().getTipoPagamentoPadrao() == TipoPagamento.PIX){
            throw new IllegalStateException("PIX não é aceito em parquimetro VARIAVEL");
        }
    }
}
