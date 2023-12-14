package com.fiap.parking.domain.controller;

import com.fiap.parking.domain.dto.CondutorDTO;
import com.fiap.parking.domain.dto.VeiculoDTO;
import com.fiap.parking.domain.service.VeiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/veiculos")
public class VeiculoController {

    @Autowired
    private VeiculoService veiculoService;
    @GetMapping("/{cpf}")
    public ResponseEntity<?> ListarTodos(@PathVariable String cpf){
        return this.veiculoService.findByCondutorCpf(cpf);
    }

    @GetMapping("/procurar-veiculo/{placa}")
    public ResponseEntity<?> Obter(@PathVariable String placa){
        return this.veiculoService.findById(placa);
    }
}
