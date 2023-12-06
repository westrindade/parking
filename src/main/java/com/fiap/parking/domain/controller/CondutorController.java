package com.fiap.parking.domain.controller;

import com.fiap.parking.domain.dto.CondutorDTO;
import com.fiap.parking.domain.service.CondutorService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<Collection<CondutorDTO>> ListarTodos(){
        return ResponseEntity.ok(this.condutorService.findAll());
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<CondutorDTO> Obter(@PathVariable String cpf){
        return ResponseEntity.ok(this.condutorService.findByCpf(cpf));
    }

    @PostMapping
    public ResponseEntity<CondutorDTO> save(@RequestBody CondutorDTO condutorDTO){
        condutorDTO = this.condutorService.save(condutorDTO);
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(condutorDTO);
    }

    @PutMapping("/{cpf}/salvarTipoPgto")
    public void savaPayment(@PathVariable String cpf,
                            @RequestParam String tipoPagamento){
        this.condutorService.savePayment(cpf,tipoPagamento);
    }
}
