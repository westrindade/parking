package com.fiap.parking.domain.controller;

import com.fiap.parking.domain.dto.CondutorDTO;
import com.fiap.parking.domain.dto.ParquimetroFixoDTO;
import com.fiap.parking.domain.dto.ParquimetroVariavelDTO;
import com.fiap.parking.domain.exception.EntidadeNaoEncontrada;
import com.fiap.parking.domain.model.*;
import com.fiap.parking.domain.service.ParquimetroService;
import com.fiap.parking.infra.utils.Utils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Parquimetro",description = "Parquimetro utilizado pelo Condutor")
@RestController
@RequestMapping("parquimetro")
public class ParquimetroController {
    @Autowired
    private ParquimetroService parquimetroService;

    @Operation(summary = "Retorna o parquimetro pelo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Retorna o parquimetro",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ParquimetroVariavelDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Parquimetro não encontrado",
                    content = { @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomExceptionHandler.class)) }),
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> ObterId(
            @Parameter(in = ParameterIn.PATH, description = "Id do parquimetro")
            @PathVariable UUID id){
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(this.parquimetroService.findById(id));
        } catch (IllegalArgumentException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @Operation(summary = "Inclui parquimetro tipo Fixo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Parquimetro incluido com sucesso",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ParquimetroFixoDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Parquimetro não encontrado",
                    content = { @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "409", description = "Erro no preenchimento",
                    content = { @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomExceptionHandler.class)) }),
    })
    @PostMapping("/fixo")
    public ResponseEntity<?> saveFixo(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados do parquimetro")
            @Valid @RequestBody ParquimetroFixoDTO parquimetroFixoDTO){
        try {
            var retorno =  this.parquimetroService.save(parquimetroFixoDTO.toParquimetro());
            return ResponseEntity.status(HttpStatus.CREATED).body(retorno);
        } catch (IllegalArgumentException|EntidadeNaoEncontrada ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (JpaSystemException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Utils.getMessage("body.atributo.chave.primaria.nao.informado"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @Operation(summary = "Inclui parquimetro tipo Variavel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Parquimetro incluido com sucesso",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ParquimetroVariavelDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Pstacionamento não encontrado",
                    content = { @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "409", description = "Erro no preenchimento",
                    content = { @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomExceptionHandler.class)) }),
    })
    @PostMapping("/variavel")
    public ResponseEntity<?> saveVariavel(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados do parquimetro")
            @RequestBody ParquimetroVariavelDTO parquimetroVariavelDTO){
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(this.parquimetroService.save(parquimetroVariavelDTO.toParquimetro()));
        } catch (IllegalArgumentException|EntidadeNaoEncontrada ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (JpaSystemException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Utils.getMessage("body.atributo.chave.primaria.nao.informado"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @Operation(summary = "Encerra o parquimetro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Parquimetro encerrado com sucesso",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ParquimetroVariavelDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Parquimetro não encontrado",
                    content = { @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomExceptionHandler.class)) }),
    })
    @PutMapping("/condutor-encerra/{id}")
    public ResponseEntity<?> condutorEncerra(
            @Parameter(in = ParameterIn.PATH, description = "Id do parquimetro")
            @PathVariable UUID id){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(this.parquimetroService.condutorInformaResposta(id));
        } catch (IllegalArgumentException|EntidadeNaoEncontrada ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @Operation(summary = "Retorna uma lista de parquimetro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Retorna a lista parquimetros",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CondutorDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Parquimetros não encontrado",
                    content = { @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomExceptionHandler.class)) }),
    })
    @GetMapping("/condutor/{cpf}")
    public ResponseEntity<?> listarParquimetroPorCondutor(@PathVariable String cpf){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(this.parquimetroService.findByCondutor(cpf));
        } catch (IllegalArgumentException|EntidadeNaoEncontrada ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @Operation(summary = "Retorna uma lista de parquimetro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Retorna a lista parquimetros",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CondutorDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Parquimetros não encontrado",
                    content = { @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomExceptionHandler.class)) }),
    })
    @GetMapping("/condutor/{cpf}/status/{status}")
    public ResponseEntity<?> listarParquimetroPorCondutorEStatus(@PathVariable String cpf,
                                                                 @NotNull @PathVariable StatusParquimetro status){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(this.parquimetroService.findByCondutorAndStatus(cpf,status));
        } catch (IllegalArgumentException|EntidadeNaoEncontrada ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
}
