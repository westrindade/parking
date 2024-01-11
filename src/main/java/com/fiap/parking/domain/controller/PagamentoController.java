package com.fiap.parking.domain.controller;

import com.fiap.parking.domain.dto.CondutorDTO;
import com.fiap.parking.domain.exception.EntidadeNaoEncontrada;
import com.fiap.parking.domain.service.PagamentoService;
import com.fiap.parking.infra.utils.Utils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Pagamento",description = "Pagamento do parquimetro realizado pelo condutor")
@RestController
@RequestMapping("/pagamento")
public class PagamentoController {

    @Autowired
    PagamentoService pagamentoService;

    @Operation(summary = "Pagar parquimetro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pagamento realizado com sucesso",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CondutorDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Parquimetro não encontrado",
                    content = { @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "409", description = "Erro no preenchimento",
                    content = { @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomExceptionHandler.class)) }),
    })
    @PostMapping("/{parquimetro_id}")
    public ResponseEntity<?> pagamento(
            @Parameter(in = ParameterIn.PATH, description = "Id do parquimetro")
            @PathVariable UUID parquimetro_id){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(this.pagamentoService.pagamento(parquimetro_id).toDTO());
        } catch (IllegalArgumentException | EntidadeNaoEncontrada ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (JpaSystemException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Utils.getMessage("body.atributo.chave.primaria.nao.informado"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
}
