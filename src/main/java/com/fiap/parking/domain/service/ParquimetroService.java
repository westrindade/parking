package com.fiap.parking.domain.service;

import com.fiap.parking.domain.dto.ParquimetroDTO;
import com.fiap.parking.domain.exception.EntidadeNaoEncontrada;
import com.fiap.parking.domain.model.*;
import com.fiap.parking.domain.repositories.ParquimetroRepository;
import com.fiap.parking.domain.service.ParquimetroService;
import com.fiap.parking.infra.utils.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ParquimetroService {

    @Autowired
    private ParquimetroRepository parquimetroRepository;
    @Autowired
    private PeriodoUtilService periodoUtilService;
    @Autowired
    private PeriodoService periodoService;
    @Autowired
    private VeiculoService veiculoService;
    @Autowired
    private CondutorService condutorService;

    private BigDecimal valorHora = BigDecimal.valueOf(8.90);

    public List<ParquimetroDTO> findAll() {
        var parquimetros = this.parquimetroRepository.findAll();
        return parquimetros.stream().map(Parquimetro::toDTO).collect(Collectors.toList());
    }

    public Parquimetro findById(UUID id) {
        return this.parquimetroRepository.findById(id)
                .orElseThrow( () -> new EntidadeNaoEncontrada("excecao.parquimetro.nao.encontrado") );
    }

    public List<ParquimetroDTO> findByStatus(String status) {
        StatusParquimetro statusCast = this.converterStringParaStatus(status);
        var parquimetros = this.parquimetroRepository.findByStatus(statusCast);
        return parquimetros.stream().map(Parquimetro::toDTO).collect(Collectors.toList());
    }

    public List<ParquimetroDTO> findByStatusAndTipoParquimetro(String status, String tipoParquimetro) {
        StatusParquimetro statusCast = this.converterStringParaStatus(status);
        TipoParquimetro tipoParquimetroCast = this.converterStringParaTipoParquimetro(tipoParquimetro);

        var parquimetros = this.parquimetroRepository.findByStatusAndTipoParquimetro(statusCast, tipoParquimetroCast);
        return parquimetros.stream().map(Parquimetro::toDTO).collect(Collectors.toList());
    }

    public ParquimetroDTO save(Parquimetro parquimetro, TipoParquimetro tipoParquimetro){
        var veiculo = this.veiculoService.findById(parquimetro.getVeiculo().getPlaca());
        var condutor = this.condutorService.findByCpf(parquimetro.getCondutor().getCpf());

        parquimetro.setValorHora(this.valorHora);
        parquimetro.setTipoParquimetro(tipoParquimetro);
        parquimetro.setStatus(StatusParquimetro.ABERTO);

        List<Periodo> periodos;
        if (TipoParquimetro.FIXO == tipoParquimetro){
            periodos = this.adicionarPeriodoFixo(parquimetro);
            parquimetro.setValorTotal(this.calcularValorTotalFixo(parquimetro));
        } else {
            periodos = new ArrayList<>();
            periodos.add(this.periodoUtilService.adicionaPeriodoVariavel(LocalDateTime.now(), parquimetro));
        }

        if (periodos.isEmpty())
            throw new IllegalArgumentException(Utils.getMessage("excecao.periodo.nao.informado"));

        parquimetro.setPeriodos(periodos);
        parquimetro.setCondutor(condutor.toCondutor());
        parquimetro.setVeiculo(veiculo.toVeiculo());

        return this.parquimetroRepository.save(parquimetro).toDTO();
    }

    

    public ParquimetroDTO condutorInformaResposta(UUID id){
        Parquimetro parquimetro = this.parquimetroRepository.findById(id)
                                            .orElseThrow( () -> new EntidadeNaoEncontrada("excecao.parquimetro.nao.encontrado") );
        parquimetro.setStatus(StatusParquimetro.ENCERRADO);
        parquimetro.setValorTotal(this.calcularValorTotalVariavel(parquimetro));

        this.encerraUltimoPeriodo(parquimetro.getPeriodos());
        parquimetro = this.parquimetroRepository.save(parquimetro);

        return parquimetro.toDTO();
    }

    private void encerraUltimoPeriodo(List<Periodo> periodos){
        Periodo ultimoPeriodo = periodoUtilService.getDataFinalMaisRecenteDaListaDePeriodos(periodos)
                .orElseThrow(() -> new EntidadeNaoEncontrada("excecao.periodo.nao.existe"));

        ultimoPeriodo.setAcaoPeriodo(AcaoPeriodo.ENCERRADO);
        this.periodoService.save(ultimoPeriodo);
    }

    private BigDecimal calcularValorTotalFixo(Parquimetro parquimetro){
        BigDecimal valorTotal = new BigDecimal("0.00");
        for (Periodo periodo : parquimetro.getPeriodos()) {
            valorTotal = this.valorHora.multiply(BigDecimal.valueOf(
                    this.periodoUtilService.calcularIntervaloHoras(periodo.getDataHoraInicial(),periodo.getDataHoraFinal())
            ));
        }
        return valorTotal;
    }

    private BigDecimal calcularValorTotalVariavel(Parquimetro parquimetro){
        int totalPeriodos = parquimetro.getPeriodos().size();
        return this.valorHora.multiply(new BigDecimal(totalPeriodos));
    }

    private List<Periodo> adicionarPeriodoFixo(Parquimetro parquimetro){
        List<Periodo> periodos = new ArrayList<>();
        for (Periodo periodo : parquimetro.getPeriodos()) {
            periodo.setAcaoPeriodo(AcaoPeriodo.ENCERRADO);
            periodos.add(periodo);
        }

        return periodos;
    }

    private StatusParquimetro converterStringParaStatus(String status) {
        try {
            return StatusParquimetro.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status desconhecido: " + status);
        }
    }

    private TipoParquimetro converterStringParaTipoParquimetro(String tipoParquimetro) {
        try {
            return TipoParquimetro.valueOf(tipoParquimetro.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("TipoParquimetro desconhecido: " + tipoParquimetro);
        }
    }

    public List<ParquimetroDTO> findByCondutor(String cpf) {
        this.condutorService.findByCpf(cpf);
        var parquimetros = this.parquimetroRepository.findByCondutor(cpf);
        return parquimetros.stream().map(Parquimetro::toDTO).collect(Collectors.toList());
    }

    public List<ParquimetroDTO> findByCondutorAndStatus(String cpf, StatusParquimetro statusParquimetro) {
        this.condutorService.findByCpf(cpf);
        var parquimetros = this.parquimetroRepository.findByCondutorAndStatus(cpf,statusParquimetro);
        return parquimetros.stream().map(Parquimetro::toDTO).collect(Collectors.toList());
    }
}