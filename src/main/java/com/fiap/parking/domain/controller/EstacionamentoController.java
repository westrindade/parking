package com.fiap.parking.domain.controller;

import com.fiap.parking.domain.dto.CondutorDTO;
import com.fiap.parking.domain.dto.EstacionamentoDTO;
import com.fiap.parking.domain.model.*;
import com.fiap.parking.domain.service.EstacionamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("estacionamento")
public class EstacionamentoController {
    @Autowired
    private EstacionamentoService estacionamentoService;

    @GetMapping
    public ResponseEntity<?> ListarTodos(){
        try {
            return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(
                    this.estacionamentoService.findAll()
            );
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> ObterId(@PathVariable UUID id){
        try {
            return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(this.estacionamentoService.findById(id));
        } catch (IllegalArgumentException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> ListarTodosPorStatus(@PathVariable String status){
        try {
            return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(
                    this.estacionamentoService.findByStatus(status)
            );
        } catch (IllegalArgumentException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @GetMapping("/status/{status}/tipo/{tipo}")
    public ResponseEntity<?> ListarTodosPorStatusETipoTempo(@PathVariable String status,
                                                             @PathVariable(value = "tipo", required = true) String tipoTempo){
        try {
            return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(
                    this.estacionamentoService.findByStatusAndTipoTempo(status,tipoTempo)
            );
        } catch (IllegalArgumentException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @PostMapping("/fixo")
    public ResponseEntity<?> saveFixo(@Valid @RequestBody EstacionamentoDTO estacionamentoDTO){
        try {
            var retorno =  this.estacionamentoService.save(estacionamentoDTO, TipoTempo.FIXO);
            return ResponseEntity.status(HttpStatus.CREATED).body(retorno);
        } catch (IllegalArgumentException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (JpaSystemException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Atributo chave primaria não informado");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @PostMapping("/variavel")
    public ResponseEntity<?> saveVariavel(@RequestBody EstacionamentoDTO estacionamentoDTO){
        try {
            var retorno =  this.estacionamentoService.save(estacionamentoDTO, TipoTempo.VARIAVEL);
            return ResponseEntity.status(HttpStatus.CREATED).body(retorno);
        } catch (IllegalArgumentException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (JpaSystemException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Atributo chave primaria não informado");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @PutMapping("/condutor-encerra/{id}")
    public ResponseEntity<?> condutorEncerra(@PathVariable UUID id){
        try{
            EstacionamentoDTO estacionamentoDTO = this.estacionamentoService.condutorInformaResposta(id);
            return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(estacionamentoDTO.valorTotal());
        } catch (IllegalArgumentException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
}
