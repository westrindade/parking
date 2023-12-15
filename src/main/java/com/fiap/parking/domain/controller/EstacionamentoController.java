package com.fiap.parking.domain.controller;

import com.fiap.parking.domain.dto.CondutorDTO;
import com.fiap.parking.domain.dto.EstacionamentoDTO;
import com.fiap.parking.domain.model.*;
import com.fiap.parking.domain.service.EstacionamentoService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Estacionamento",description = "Estacionamento utilizado pelo Condutor")
@RestController
@RequestMapping("estacionamento")
public class EstacionamentoController {
    @Autowired
    private EstacionamentoService estacionamentoService;

    @ApiOperation(value = "Retorna uma lista de parquimetro utilizados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Retorna a lista de estacionamento",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = EstacionamentoDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomExceptionHandler.class)) }),
    })
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

    @ApiOperation(value = "Retorna o estacionamento pelo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Retorna o estacionamento",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = EstacionamentoDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Estacionamento não encontrado",
                    content = { @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomExceptionHandler.class)) }),
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> ObterId(
            @Parameter(in = ParameterIn.PATH, description = "Id do Estacionamento")
            @PathVariable UUID id){
        try {
            return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(this.estacionamentoService.findById(id));
        } catch (IllegalArgumentException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @ApiOperation(value = "Retorna uma lista de estacionamento pelo status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Retorna a lista de estacionamento",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = EstacionamentoDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Estacionamento não encontrado",
                    content = { @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomExceptionHandler.class)) }),
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<?> ListarTodosPorStatus(
            @Parameter(in = ParameterIn.PATH, description = "Status do Estacionamento")
            @PathVariable String status){
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

    @ApiOperation(value = "Retorna uma lista de estacionamento pelo status e tipo tempo (fixo,variavel)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Retorna a lista de estacionamento",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = EstacionamentoDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Estacionamento não encontrado",
                    content = { @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomExceptionHandler.class)) }),
    })
    @GetMapping("/status/{status}/tipo/{tipo}")
    public ResponseEntity<?> ListarTodosPorStatusETipoTempo(
            @Parameter(in = ParameterIn.PATH, description = "Status do Estacionamento")
            @PathVariable String status,
            @Parameter(in = ParameterIn.PATH, description = "Tipo de Estacionamento")
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

    @ApiOperation(value = "Inclui estacionamento tipo Fixo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Estacionamento incluido com sucesso",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = EstacionamentoDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Estacionamento não encontrado",
                    content = { @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "409", description = "Erro no preenchimento",
                    content = { @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomExceptionHandler.class)) }),
    })
    @PostMapping("/fixo")
    public ResponseEntity<?> saveFixo(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados do Estacionamento")
            @Valid @RequestBody EstacionamentoDTO estacionamentoDTO){
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

    @ApiOperation(value = "Inclui estacionamento tipo Variavel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Estacionamento incluido com sucesso",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = EstacionamentoDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Estacionamento não encontrado",
                    content = { @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "409", description = "Erro no preenchimento",
                    content = { @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomExceptionHandler.class)) }),
    })
    @PostMapping("/variavel")
    public ResponseEntity<?> saveVariavel(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados do Estacionamento")
            @RequestBody EstacionamentoDTO estacionamentoDTO){
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

    @ApiOperation(value = "Encerra o estacinoamento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Estacionamento encerrado com sucesso",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = EstacionamentoDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Estacionamento não encontrado",
                    content = { @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomExceptionHandler.class)) }),
    })
    @PutMapping("/condutor-encerra/{id}")
    public ResponseEntity<?> condutorEncerra(
            @Parameter(in = ParameterIn.PATH, description = "Id do estacionamento")
            @PathVariable UUID id){
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
