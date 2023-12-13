package com.fiap.parking.domain.controller;

import com.fiap.parking.domain.dto.EstacionamentoDTO;
import com.fiap.parking.domain.model.TipoTempo;
import com.fiap.parking.domain.service.EstacionamentoService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<Collection<EstacionamentoDTO>> ListarTodos(){
        return ResponseEntity.ok(this.estacionamentoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstacionamentoDTO> ObterId(@PathVariable UUID id){
        return ResponseEntity.ok(this.estacionamentoService.findById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Collection<EstacionamentoDTO>> ListarTodosPorStatus(@PathVariable String status){
        return ResponseEntity.ok(this.estacionamentoService.findByStatus(status));
    }

    @GetMapping("/status/{status}/tipo/{tipo}")
    public ResponseEntity<Collection<EstacionamentoDTO>> ListarTodosPorStatusETipoTempo(@PathVariable String status,
                                                                                        @PathVariable(value = "tipo", required = true) String tipoTempo){
        return ResponseEntity.ok(this.estacionamentoService.findByStatusAndTipoTempo(status,tipoTempo));
    }

    @PostMapping("/fixo")
    public ResponseEntity<EstacionamentoDTO> saveFixo(@RequestBody EstacionamentoDTO estacionamentoDTO){
        estacionamentoDTO = this.estacionamentoService.save(estacionamentoDTO, TipoTempo.FIXO);
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(estacionamentoDTO);
    }

    @PostMapping("/variavel")
    public ResponseEntity<EstacionamentoDTO> saveVariavel(@RequestBody EstacionamentoDTO estacionamentoDTO){
        estacionamentoDTO = this.estacionamentoService.save(estacionamentoDTO, TipoTempo.VARIAVEL);
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(estacionamentoDTO);
    }

    @PutMapping("/condutor-encerra/{id}")
    public ResponseEntity<BigDecimal> saveVariavel(@PathVariable UUID id){
        EstacionamentoDTO estacionamentoDTO = this.estacionamentoService.condutorInformaResposta(id);
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(estacionamentoDTO.valorTotal());
    }
}
