package com.fiap.parking.domain.controller;

import com.fiap.parking.domain.dto.CondutorDTO;
import com.fiap.parking.domain.exception.EntidadeNaoEncontrada;
import com.fiap.parking.domain.model.TipoPagamento;
import com.fiap.parking.domain.service.CondutorService;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Condutor", description = "Condutor que utilizara o parquimetro")
@RestController
@RequestMapping("/condutores")
public class CondutorController {

    @Autowired
    private CondutorService condutorService;

    @Operation(summary = "Retorna uma lista de condutores")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Retorna a lista de condutores",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CondutorDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomExceptionHandler.class)) }),
    })
    @GetMapping
    public ResponseEntity<?> ListarTodos(){
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(this.condutorService.findAll());
        } catch (Exception ex){
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
        }
    }

    @Operation(summary = "Retorna um condutor pelo cpf informado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Retorna a lista de condutores",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CondutorDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Condutor não encontrado",
                    content = { @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomExceptionHandler.class)) }),
    })
    @GetMapping("/{cpf}")
    public ResponseEntity<?> Obter(
            @Parameter(in = ParameterIn.PATH, description = "CPF do condutor")
            @PathVariable String cpf){
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(this.condutorService.findByCpf(cpf));
        } catch (IllegalArgumentException|EntidadeNaoEncontrada ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    
    @Operation(summary = "Inclui um condutor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Condutor incluido com sucesso",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CondutorDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Condutor não encontrado",
                    content = { @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "409", description = "Erro no preenchimento",
                    content = { @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomExceptionHandler.class)) }),
    })
    @PostMapping
    public ResponseEntity<?> save(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados do Condutor incluindo veiculo(s)")
            @Valid @RequestBody CondutorDTO condutorDTO){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(this.condutorService.save(condutorDTO));
        } catch (IllegalArgumentException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (JpaSystemException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Atributo chave primaria não informado");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @Operation(summary = "Salva o tipo de pagamento para o condutor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tipo de Pagamento incluido",
                    content = { @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "404", description = "Condutor não encontrado",
                    content = { @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomExceptionHandler.class)) }),
    })
    @PutMapping("/{cpf}/salvarTipoPgto")
    public ResponseEntity<?> savaPayment(
        @Parameter(in = ParameterIn.PATH, description = "CPF do condutor")
        @PathVariable String cpf,
        @NotNull @RequestParam(name = "tipoPagamento", defaultValue = "") TipoPagamento tipoPagamento
    ){
        try {
            this.condutorService.savePayment(cpf, tipoPagamento);
            return ResponseEntity.status(HttpStatus.CREATED).body("Tipo de Pagamento incluido ao condutor");
        } catch (IllegalArgumentException|EntidadeNaoEncontrada ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }

    }
}
