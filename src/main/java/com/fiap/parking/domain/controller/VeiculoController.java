package com.fiap.parking.domain.controller;

import com.fiap.parking.domain.dto.CondutorDTO;
import com.fiap.parking.domain.dto.VeiculoDTO;
import com.fiap.parking.domain.service.VeiculoService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Veiculo",description = "Veiculos cadastrados pelo condutor")
@RestController
@RequestMapping("/veiculos")
public class VeiculoController {

    @Autowired
    private VeiculoService veiculoService;

    @ApiOperation(value = "Retorna uma lista de veiculos com o condutor informado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Retorna a lista de veiculos com o condutor informado",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CondutorDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Condutor não encontrado",
                    content = { @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomExceptionHandler.class)) }),
    })
    @GetMapping("/{cpf}")
    public ResponseEntity<?> ListarTodos(
            @Parameter(in = ParameterIn.PATH, description = "CPF do condutor")
            @PathVariable String cpf){
        try {
            return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(this.veiculoService.findByCondutorCpf(cpf));
        } catch (IllegalArgumentException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @ApiOperation(value = "Retorna condutor dono do veiculo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Retorna condutor dono do veiculo",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = VeiculoDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Veiculo não encontrado",
                    content = { @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomExceptionHandler.class)) }),
    })
    @GetMapping("/procurar-veiculo/{placa}")
    public ResponseEntity<?> Obter(
            @Parameter(in = ParameterIn.PATH, description = "Placa do veiculo")
            @PathVariable String placa){
        try {
            return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(this.veiculoService.findById(placa));
        } catch (IllegalArgumentException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
}
