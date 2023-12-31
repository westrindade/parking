package com.fiap.parking.domain.controller;

import com.fiap.parking.domain.exception.EntidadeNaoEncontrada;
import com.fiap.parking.domain.service.PeriodoService;
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
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Periodo",description = "Periodo que o veiculo ficara parquimetro")
@RestController
@RequestMapping("/periodo")
public class PeriodoController {
    @Autowired
    private PeriodoService periodoService;

    @Operation(summary = "Inclui Periodo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Periodo salvo com sucesso",
                    content = { @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "404", description = "Parquimetro não encontrado",
                    content = { @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "409", description = "Erro no preenchimento",
                    content = { @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CustomExceptionHandler.class)) }),
    })
    @PostMapping("/{parquimetro_id}")
    public ResponseEntity<?> save(
            @Parameter(in = ParameterIn.PATH, description = "Id do parquimetro")
            @PathVariable UUID parquimetro_id){
        try{
            this.periodoService.save(parquimetro_id);

            return ResponseEntity.status(HttpStatus.CREATED).body(Utils.getMessage("body.periodo.salvo.sucesso"));
        } catch (IllegalArgumentException|EntidadeNaoEncontrada ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (JpaSystemException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Utils.getMessage("body.atributo.chave.primaria.nao.informado"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
}
