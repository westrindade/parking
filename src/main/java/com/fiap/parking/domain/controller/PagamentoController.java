package com.fiap.parking.domain.controller;

import com.fiap.parking.domain.dto.PagamentoDTO;
import com.fiap.parking.domain.service.PagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/pagamento")
public class PagamentoController {

    @Autowired
    PagamentoService pagamentoService;
    @PostMapping("/{id}")
    public ResponseEntity<?> pagamento(@PathVariable UUID id){
        return this.pagamentoService.pagamento(id);
    }
}
