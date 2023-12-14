package com.fiap.parking.domain.controller;

import com.fiap.parking.domain.dto.EstacionamentoDTO;
import com.fiap.parking.domain.model.TipoTempo;
import com.fiap.parking.domain.service.EstacionamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping("estacionamento")
public class EstacionamentoController {
    @Autowired
    private EstacionamentoService estacionamentoService;

    @GetMapping
    public ResponseEntity<?> ListarTodos(){
        return this.estacionamentoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> ObterId(@PathVariable UUID id){
        return this.estacionamentoService.findById(id);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> ListarTodosPorStatus(@PathVariable String status){
        return this.estacionamentoService.findByStatus(status);
    }

    @GetMapping("/status/{status}/tipo/{tipo}")
    public ResponseEntity<?> ListarTodosPorStatusETipoTempo(@PathVariable String status,
                                                                                        @PathVariable(value = "tipo", required = true) String tipoTempo){
        return this.estacionamentoService.findByStatusAndTipoTempo(status,tipoTempo);
    }

    @PostMapping("/fixo")
    public ResponseEntity<?> saveFixo(@Valid @RequestBody EstacionamentoDTO estacionamentoDTO){
        return this.estacionamentoService.save(estacionamentoDTO, TipoTempo.FIXO);
    }

    @PostMapping("/variavel")
    public ResponseEntity<?> saveVariavel(@RequestBody EstacionamentoDTO estacionamentoDTO){
        return this.estacionamentoService.save(estacionamentoDTO, TipoTempo.VARIAVEL);
    }

    @PutMapping("/condutor-encerra/{id}")
    public ResponseEntity<?> saveVariavel(@PathVariable UUID id){
        return this.estacionamentoService.condutorInformaResposta(id);
    }
}
