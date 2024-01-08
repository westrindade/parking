package com.fiap.parking.domain.controller;

import com.fiap.parking.domain.dto.ParquimetroDTO;
import com.fiap.parking.domain.model.*;
import com.fiap.parking.domain.service.ParquimetroService;
import com.fiap.parking.utils.Utils;
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

@Tag(name = "Parquimetro",description = "Parquimetro utilizado pelo Condutor")
@RestController
@RequestMapping("parquimetro")
public class ParquimetroController {
    @Autowired
    private ParquimetroService parquimetroService;

    @ApiOperation(value = "Retorna uma lista de parquimetro utilizados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Retorna a lista de parquimetro",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ParquimetroDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomExceptionHandler.class)) }),
    })
    @GetMapping
    public ResponseEntity<?> ListarTodos(){
        try {
            return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(
                    this.parquimetroService.findAll()
            );
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @ApiOperation(value = "Retorna o parquimetro pelo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Retorna o parquimetro",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ParquimetroDTO.class)) }),
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
            return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(this.parquimetroService.findById(id));
        } catch (IllegalArgumentException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @ApiOperation(value = "Retorna uma lista de parquimetro pelo status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Retorna a lista de parquimetro",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ParquimetroDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Parquimetro não encontrado",
                    content = { @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomExceptionHandler.class)) }),
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<?> ListarTodosPorStatus(
            @Parameter(in = ParameterIn.PATH, description = "Status do parquimetro")
            @PathVariable String status){
        try {
            return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(
                    this.parquimetroService.findByStatus(status)
            );
        } catch (IllegalArgumentException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @ApiOperation(value = "Retorna uma lista de parquimetro pelo status e tipo tempo (fixo,variavel)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Retorna a lista de parquimetro",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ParquimetroDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Parquimetro não encontrado",
                    content = { @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomExceptionHandler.class)) }),
    })
    @GetMapping("/status/{status}/tipo/{tipo}")
    public ResponseEntity<?> ListarTodosPorStatusETipoParquimetro(
            @Parameter(in = ParameterIn.PATH, description = "Status do parquimetro")
            @PathVariable String status,
            @Parameter(in = ParameterIn.PATH, description = "Tipo de parquimetro")
            @PathVariable(value = "tipo", required = true) String tipoParquimetro){
        try {
            return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(
                    this.parquimetroService.findByStatusAndTipoParquimetro(status,tipoParquimetro)
            );
        } catch (IllegalArgumentException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @ApiOperation(value = "Inclui parquimetro tipo Fixo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Parquimetro incluido com sucesso",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ParquimetroDTO.class)) }),
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
            @Valid @RequestBody ParquimetroDTO parquimetroDTO){
        try {
            var retorno =  this.parquimetroService.save(parquimetroDTO, TipoParquimetro.FIXO);
            return ResponseEntity.status(HttpStatus.CREATED).body(retorno);
        } catch (IllegalArgumentException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (JpaSystemException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Utils.getMessage("chave.primaria.nao.informada"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @ApiOperation(value = "Inclui parquimetro tipo Variavel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Parquimetro incluido com sucesso",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ParquimetroDTO.class)) }),
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
            @RequestBody ParquimetroDTO oarParquimetroDTO){
        try {
            var retorno =  this.parquimetroService.save(oarParquimetroDTO, TipoParquimetro.VARIAVEL);
            return ResponseEntity.status(HttpStatus.CREATED).body(retorno);
        } catch (IllegalArgumentException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (JpaSystemException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Utils.getMessage("chave.primaria.nao.informada"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @ApiOperation(value = "Encerra o parquimetro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Parquimetro encerrado com sucesso",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ParquimetroDTO.class)) }),
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
            ParquimetroDTO parquimetroDTO = this.parquimetroService.condutorInformaResposta(id);
            return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(parquimetroDTO.valorTotal());
        } catch (IllegalArgumentException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
}
