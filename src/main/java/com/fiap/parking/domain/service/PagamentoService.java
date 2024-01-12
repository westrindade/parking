package com.fiap.parking.domain.service;

import com.fiap.parking.domain.model.Parquimetro;
import com.fiap.parking.domain.model.Pagamento;
import com.fiap.parking.domain.model.TipoPagamento;
import com.fiap.parking.domain.model.TipoParquimetro;
import com.fiap.parking.domain.repositories.PagamentoRepository;
import com.fiap.parking.domain.service.PagamentoService;
import com.fiap.parking.infra.utils.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;
    @Autowired
    private ParquimetroService parquimetroService;

    public Pagamento pagamento(UUID parquimetro_id) {
        Parquimetro parquimetro = this.parquimetroService.findById(parquimetro_id);
        if (parquimetro.getTipoParquimetro() == TipoParquimetro.VARIAVEL)
            this.tipoParquimetroVariavel(parquimetro);
        else
            this.parquimetroService.condutorInformaResposta(parquimetro_id);

        return this.pagamentoRepository.save(new Pagamento(parquimetro));
    }

    private void tipoParquimetroVariavel(Parquimetro parquimetro){
        if (parquimetro.getCondutor().getTipoPagamentoPadrao() == TipoPagamento.PIX){
            throw new IllegalStateException(Utils.getMessage("excecao.pix.nao.aceito.parquimetro.variavel"));
        }
    }
}
