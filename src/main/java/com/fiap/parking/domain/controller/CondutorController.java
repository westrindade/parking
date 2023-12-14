package com.fiap.parking.domain.controller;

import com.fiap.parking.domain.dto.CondutorDTO;
import com.fiap.parking.domain.service.CondutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
@RestController
@RequestMapping("/condutores")
public class CondutorController {

    @Autowired
    private CondutorService condutorService;

    @GetMapping
    public ResponseEntity<?> ListarTodos(){
        return this.condutorService.findAll();
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<?> Obter(@PathVariable String cpf){
        return this.condutorService.findByCpf(cpf);
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody CondutorDTO condutorDTO){
        return this.condutorService.save(condutorDTO);
    }

    @PutMapping("/{cpf}/salvarTipoPgto")
    public ResponseEntity<?> savaPayment(@PathVariable String cpf,
                            @RequestParam String tipoPagamento){
        return this.condutorService.savePayment(cpf,tipoPagamento);
    }
}
