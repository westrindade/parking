package com.fiap.parking.domain.controller;

import com.fiap.parking.domain.dto.PagamentoDTO;
import com.fiap.parking.domain.model.Estacionamento;
import com.fiap.parking.domain.model.Pagamento;
import com.fiap.parking.domain.model.StatusPagamento;
import com.fiap.parking.domain.service.PagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/pagamento")
public class PagamentoController {

    @Autowired
    PagamentoService pagamentoService;
    @PostMapping("/{estacionamento_id}")
    public ResponseEntity<?> pagamento(@PathVariable UUID estacionamento_id){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(this.pagamentoService.pagamento(estacionamento_id));
        } catch (IllegalArgumentException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (JpaSystemException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Atributo chave primaria não informado");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
}
