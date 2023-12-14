package com.fiap.parking.domain.service.impl;

import com.fiap.parking.domain.dto.PagamentoDTO;
import com.fiap.parking.domain.model.Estacionamento;
import com.fiap.parking.domain.model.Pagamento;
import com.fiap.parking.domain.model.StatusPagamento;
import com.fiap.parking.domain.model.TipoPagamento;
import com.fiap.parking.domain.repositories.EstacionamentoRepository;
import com.fiap.parking.domain.repositories.PagamentoRepository;
import com.fiap.parking.domain.service.PagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PagamentoServiceImpl implements PagamentoService {

    @Autowired
    private EstacionamentoRepository estacionamentoRepository;
    @Autowired
    private PagamentoRepository pagamentoRepository;
    @Override
    public ResponseEntity<?> pagamento(UUID idEstacionamento) {

        try{
            Estacionamento estacionamento = this.estacionamentoRepository.findById(idEstacionamento)
                    .orElseThrow(()-> new IllegalArgumentException("Estacionamento nao encontrado"));

            this.variavel(estacionamento);

            Pagamento pagamento = new Pagamento();
            pagamento.setTipoPagamento(estacionamento.getCondutor().getTipoPagamentoPadrao());
            pagamento.setStatus(StatusPagamento.SUCESSO);
            pagamento.setEstacionamento(estacionamento);
            pagamento.setValor(estacionamento.getValorTotal());
            pagamento.setDataHora(LocalDateTime.now());

            this.pagamentoRepository.save(pagamento);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(this.toPagamentoDTO(pagamento));
        } catch (IllegalArgumentException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (JpaSystemException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Atributo chave primaria não informado");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    private void variavel(Estacionamento estacionamento){
        if (estacionamento.getCondutor().getTipoPagamentoPadrao() == TipoPagamento.PIX){
            throw new IllegalStateException("PIX não é aceito em estadia VARIAVEL");
        }
    }

    private PagamentoDTO toPagamentoDTO(Pagamento pagamento) {
        return new PagamentoDTO(
                pagamento.getId(),
                pagamento.getStatus(),
                pagamento.getDataHora(),
                pagamento.getTipoPagamento(),
                pagamento.getValor(),
                pagamento.getEstacionamento()
        );
    }
}
