package com.fiap.parking.domain.service.impl;

import com.fiap.parking.domain.dto.PeriodoDTO;
import com.fiap.parking.domain.model.Estacionamento;
import com.fiap.parking.domain.model.Periodo;
import com.fiap.parking.domain.repositories.EstacionamentoRepository;
import com.fiap.parking.domain.repositories.PeriodoRepository;
import com.fiap.parking.domain.service.PeriodoService;
import com.fiap.parking.domain.service.PeriodoUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PeriodoServiceImpl implements PeriodoService {
    @Autowired
    private PeriodoRepository periodoRepository;
    @Autowired
    private EstacionamentoRepository estacionamentoRepository;
    @Autowired
    private PeriodoUtilService periodoUtilService;
    @Override
    public ResponseEntity<?> save(UUID estacionamento_id) {
        try{
            Estacionamento estacionamento = this.estacionamentoRepository.findById(estacionamento_id)
                    .orElseThrow(() -> new IllegalArgumentException("Estacionamento não existe"));

            Optional<Periodo> ultimoPeriodo = this.periodoUtilService.ordenarDecrescentePegarPrimeiro(estacionamento.getPeriodos());

            this.periodoRepository.save(this.periodoUtilService.addHoraPeriodo(ultimoPeriodo.get().getDataHoraFinal(),estacionamento));

            return ResponseEntity.status(HttpStatus.CREATED).body("Periodo salvo com sucesso");
        } catch (IllegalArgumentException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (JpaSystemException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Atributo chave primaria não informado");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

}
