package com.fiap.parking.domain.service.impl;

import com.fiap.parking.domain.dto.EstacionamentoDTO;
import com.fiap.parking.domain.model.*;
import com.fiap.parking.domain.repositories.CondutorRepository;
import com.fiap.parking.domain.repositories.EstacionamentoRepository;
import com.fiap.parking.domain.repositories.VeiculoRepository;
import com.fiap.parking.domain.service.EstacionamentoService;
import com.fiap.parking.domain.service.PeriodoUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Value;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EstacionamentoServiceImpl implements EstacionamentoService {

    @Autowired
    private EstacionamentoRepository estacionamentoRepository;
    @Autowired
    private VeiculoRepository veiculoRepository;
    @Autowired
    private CondutorRepository condutorRepository;
    @Autowired
    private PeriodoUtilService periodoUtilService;

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
    public EstacionamentoDTO saveFixo(EstacionamentoDTO estacionamentoDTO) {
        Estacionamento estacionamento = toEstacionamento(estacionamentoDTO);
        estacionamento.setValorHora(this.valorHora);
        estacionamento.setTipoTempo(TipoTempo.FIXO);
        estacionamento.setStatus(StatusEstacionamento.ABERTO);

        var veiculo = this.veiculoRepository.findById(estacionamentoDTO.veiculo())
                                .orElseThrow( () -> new IllegalArgumentException("Veiculo não encontrado") );;
        var condutor =  this.condutorRepository.findById(estacionamentoDTO.condutor())
                                .orElseThrow( () -> new IllegalArgumentException("Condutor não encontrado") );

        List<Periodo> periodos = this.addPeriodoFixo(estacionamentoDTO,estacionamento);
        if (periodos.isEmpty()) {
            throw new IllegalArgumentException("Período não informado");
        }
        BigDecimal valorTotal = this.calcularValorTotal(estacionamentoDTO);

        estacionamento.setValorTotal(valorTotal);
        estacionamento.setPeriodos(periodos);
        estacionamento.setCondutor(condutor);
        estacionamento.setVeiculo(veiculo);

        return this.toEstacionamentoDTO(this.estacionamentoRepository.save(estacionamento));
    }

    @Override
    public EstacionamentoDTO saveVariavel(EstacionamentoDTO estacionamentoDTO) {
        Estacionamento estacionamento = toEstacionamento(estacionamentoDTO);
        estacionamento.setValorHora(this.valorHora);
        estacionamento.setTipoTempo(TipoTempo.VARIAVEL);
        estacionamento.setStatus(StatusEstacionamento.ABERTO);

        var veiculo = this.veiculoRepository.findById(estacionamentoDTO.veiculo())
                .orElseThrow( () -> new IllegalArgumentException("Veiculo não encontrado") );;
        var condutor =  this.condutorRepository.findById(estacionamentoDTO.condutor())
                .orElseThrow( () -> new IllegalArgumentException("Condutor não encontrado") );

        LocalDateTime dataInicial = LocalDateTime.now();
        List<Periodo> periodos = new ArrayList<>();
        periodos.add(this.periodoUtilService.addHoraPeriodo(dataInicial,estacionamento));

        estacionamento.setPeriodos(periodos);
        estacionamento.setCondutor(condutor);
        estacionamento.setVeiculo(veiculo);

        return this.toEstacionamentoDTO(this.estacionamentoRepository.save(estacionamento));
    }

    private BigDecimal calcularValorTotal(EstacionamentoDTO estacionamentoDTO){
        BigDecimal valorTotal = new BigDecimal("0.00");
        for (Periodo periodo : estacionamentoDTO.periodos()) {

            valorTotal = this.valorHora.multiply(BigDecimal.valueOf(
                    this.periodoUtilService.calcularIntervaloHoras(periodo.getDataHoraInicial(),periodo.getDataHoraFinal())
            ));
        }

        return valorTotal;
    }

    private List<Periodo> addPeriodoFixo(EstacionamentoDTO estacionamentoDTO,Estacionamento estacionamento){
        List<Periodo> periodos = new ArrayList<>();
        for (Periodo periodo : estacionamentoDTO.periodos()) {
            periodo.setEstacionamento(estacionamento);

            AcaoPeriodo acaoPeriodo = null;
            acaoPeriodo = AcaoPeriodo.ENCERRADO;

            periodo.setAcaoPeriodo(acaoPeriodo);
            periodos.add(periodo);
        }

        return periodos;
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
