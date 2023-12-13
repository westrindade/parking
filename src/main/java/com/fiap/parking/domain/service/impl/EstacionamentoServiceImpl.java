package com.fiap.parking.domain.service.impl;

import com.fiap.parking.domain.dto.EstacionamentoDTO;
import com.fiap.parking.domain.model.*;
import com.fiap.parking.domain.repositories.CondutorRepository;
import com.fiap.parking.domain.repositories.EstacionamentoRepository;
import com.fiap.parking.domain.repositories.VeiculoRepository;
import com.fiap.parking.domain.service.EstacionamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Value;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EstacionamentoServiceImpl implements EstacionamentoService {

    @Autowired
    private EstacionamentoRepository estacionamentoRepository;
    @Autowired
    private VeiculoRepository veiculoRepository;
    @Autowired
    private CondutorRepository condutorRepository;


    @Value("${estacionamento.valorHora}")
    private BigDecimal valorHora;
    @Override
    public List<EstacionamentoDTO> findAll() {
        var estacionamento = this.estacionamentoRepository.findAll();
        return estacionamento.stream().map(this::toEstacionamentoDTO).collect(Collectors.toList());
    }

    @Override
    public EstacionamentoDTO findById(UUID id) {
        var estacionamento = this.toEstacionamentoDTO(this.estacionamentoRepository.findById(id)
                .orElseThrow( () -> new IllegalArgumentException("Estacionamento não encontrado") ) );
        return estacionamento;
    }

    @Override
    public List<EstacionamentoDTO> findByStatus(String status) {
        StatusEstacionamento statusCast = this.converterStringParaStatus(status);
        var estacionamento = this.estacionamentoRepository.findByStatus(statusCast);
        return estacionamento.stream().map(this::toEstacionamentoDTO).collect(Collectors.toList());
    }

    @Override
    public List<EstacionamentoDTO> findByStatusAndTipoTempo(String status, String tipoTempo) {
        StatusEstacionamento statusCast = this.converterStringParaStatus(status);
        TipoTempo tipoTempoCast = this.converterStringParaTipoTempo(tipoTempo);
        var estacionamento = this.estacionamentoRepository.findByStatusAndTipoTempo(statusCast,tipoTempoCast);
        return estacionamento.stream().map(this::toEstacionamentoDTO).collect(Collectors.toList());
    }

    @Override
    public EstacionamentoDTO save(EstacionamentoDTO estacionamentoDTO) {
        Estacionamento estacionamento = toEstacionamento(estacionamentoDTO);
        estacionamento.setValorHora(this.valorHora);
        estacionamento.setStatus(StatusEstacionamento.ABERTO);

        var veiculo = this.veiculoRepository.findById(estacionamentoDTO.veiculo())
                                .orElseThrow( () -> new IllegalArgumentException("Veiculo não encontrado") );;
        var condutor =  this.condutorRepository.findById(estacionamentoDTO.condutor())
                                .orElseThrow( () -> new IllegalArgumentException("Condutor não encontrado") );

        BigDecimal valorTotal = new BigDecimal("0.00");
        List<Periodo> periodos = new ArrayList<>();
        for (Periodo periodo : estacionamentoDTO.periodos()) {
            periodo.setEstacionamento(estacionamento);

            AcaoPeriodo acaoPeriodo = null;
            if ("FIXO".equals(estacionamentoDTO.tipoTempo().toString())) {
                acaoPeriodo = AcaoPeriodo.ENCERRADO;
                valorTotal = this.valorHora.multiply(BigDecimal.valueOf(
                        this.calcularIntervaloHoras(periodo.getDataHoraInicial(),periodo.getDataHoraFinal())
                ));
            }
            periodo.setAcaoPeriodo(acaoPeriodo);
            periodos.add(periodo);
        }
        estacionamento.setValorTotal(valorTotal);
        estacionamento.setPeriodos(periodos);
        estacionamento.setCondutor(condutor);
        estacionamento.setVeiculo(veiculo);

        return this.toEstacionamentoDTO(this.estacionamentoRepository.save(estacionamento));
    }

    private long calcularIntervaloHoras(LocalDateTime dataInicio, LocalDateTime dataFim){
        Duration duracao = Duration.between(dataInicio, dataFim);
        return duracao.toHours() == 0 ? 1 : duracao.toHours() ;
    }

    private EstacionamentoDTO toEstacionamentoDTO(Estacionamento estacionamento) {
        return new EstacionamentoDTO(
                estacionamento.getIdEstacionamento(),
                estacionamento.getTipoTempo(),
                estacionamento.getVeiculo().getPlaca(),
                estacionamento.getCondutor().getCpf(),
                estacionamento.getLongitude(),
                estacionamento.getLatitude(),
                estacionamento.getValorHora(),
                estacionamento.getValorTotal(),
                estacionamento.getStatus(),
                estacionamento.getPeriodos()
        );
    }

    private Estacionamento toEstacionamento(EstacionamentoDTO estacionamentoDTO) {
        return new Estacionamento(
                estacionamentoDTO.tipoTempo(),
                estacionamentoDTO.latitude(),
                estacionamentoDTO.longitude(),
                estacionamentoDTO.valorHora(),
                estacionamentoDTO.valorTotal(),
                estacionamentoDTO.status()
        );
    }

    private StatusEstacionamento converterStringParaStatus(String status) {
        try {
            return StatusEstacionamento.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status desconhecido: " + status);
        }
    }

    private TipoTempo converterStringParaTipoTempo(String tipoTempo) {
        try {
            return TipoTempo.valueOf(tipoTempo.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("TipoTempo desconhecido: " + tipoTempo);
        }
    }
}
