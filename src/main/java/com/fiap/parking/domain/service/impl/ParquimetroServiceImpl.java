package com.fiap.parking.domain.service.impl;

import com.fiap.parking.domain.dto.ParquimetroDTO;
import com.fiap.parking.domain.model.*;
import com.fiap.parking.domain.repositories.CondutorRepository;
import com.fiap.parking.domain.repositories.ParquimetroRepository;
import com.fiap.parking.domain.repositories.PeriodoRepository;
import com.fiap.parking.domain.repositories.VeiculoRepository;
import com.fiap.parking.domain.service.ParquimetroService;
import com.fiap.parking.domain.service.PeriodoUtilService;
import com.fiap.parking.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Value;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ParquimetroServiceImpl implements ParquimetroService {

    @Autowired
    private ParquimetroRepository parquimetroRepository;
    @Autowired
    private VeiculoRepository veiculoRepository;
    @Autowired
    private CondutorRepository condutorRepository;
    @Autowired
    private PeriodoUtilService periodoUtilService;
    @Autowired
    private PeriodoRepository periodoRepository;

    @Value("${parquimetro.valorHora}")
    private BigDecimal valorHora;

    @Override
    public List<ParquimetroDTO> findAll() {
        var parquimetros = this.parquimetroRepository.findAll();
        return parquimetros.stream().map(this::toParquimetroDTO).collect(Collectors.toList());
    }

    @Override
    public ParquimetroDTO findById(UUID id) {
        var parquimetros = this.toParquimetroDTO(this.parquimetroRepository.findById(id)
                .orElseThrow( () -> new IllegalArgumentException(Utils.getMessage("parquimetro.nao.encontrado")) ) );
        return parquimetros;
    }

    @Override
    public List<ParquimetroDTO> findByStatus(String status) {
        StatusParquimetro statusCast = this.converterStringParaStatus(status);
        var parquimetros = this.parquimetroRepository.findByStatus(statusCast);
        return parquimetros.stream().map(this::toParquimetroDTO).collect(Collectors.toList());
    }

    @Override
    public List<ParquimetroDTO> findByStatusAndTipoParquimetro(String status, String tipoParquimetro) {
        StatusParquimetro statusCast = this.converterStringParaStatus(status);
        TipoParquimetro tipoParquimetroCast = this.converterStringParaTipoParquimetro(tipoParquimetro);

        var parquimetros = this.parquimetroRepository.findByStatusAndTipoParquimetro(statusCast, tipoParquimetroCast);
        return parquimetros.stream().map(this::toParquimetroDTO).collect(Collectors.toList());
    }

    @Override
    public ParquimetroDTO save(ParquimetroDTO parquimetroDTO, TipoParquimetro tipoParquimetro){

        var veiculo = this.veiculoRepository.findById(parquimetroDTO.veiculo())
                .orElseThrow( () -> new IllegalArgumentException(Utils.getMessage("veiculo.nao.encontrado")));
        var condutor =  this.condutorRepository.findById(parquimetroDTO.condutor())
                .orElseThrow( () -> new IllegalArgumentException(Utils.getMessage("condutor.nao.encontrado")) );

        Parquimetro parquimetro = toParquimetro(parquimetroDTO);
        parquimetro.setValorHora(this.valorHora);
        parquimetro.setTipoParquimetro(tipoParquimetro);
        parquimetro.setStatus(StatusParquimetro.ABERTO);

        List<Periodo> periodos;
        if (TipoParquimetro.FIXO == tipoParquimetro){
            periodos = this.adicionarPeriodoFixo(parquimetro);
            parquimetro.setValorTotal(this.calcularValorTotalFixo(parquimetroDTO));
        } else {
            LocalDateTime dataInicial = LocalDateTime.now();
            periodos = new ArrayList<>();
            periodos.add(this.periodoUtilService.adicionaPeriodoVariavel(dataInicial,parquimetro));
        }

        if (periodos.isEmpty())
            throw new IllegalArgumentException(Utils.getMessage("periodo.nao.informado"));

        parquimetro.setPeriodos(periodos);
        parquimetro.setCondutor(condutor);
        parquimetro.setVeiculo(veiculo);

        return this.toParquimetroDTO(this.parquimetroRepository.save(parquimetro));
    }

    @Override
    public ParquimetroDTO condutorInformaResposta(UUID id){

        Parquimetro parquimetro = this.parquimetroRepository.findById(id)
                                            .orElseThrow( () -> new IllegalArgumentException(Utils.getMessage("parquimetro.nao.encontrado")) );
        parquimetro.setStatus(StatusParquimetro.ENCERRADO);
        parquimetro.setValorTotal(this.calcularValorTotalVariavel(parquimetro));

        this.encerraUltimoPeriodo(parquimetro.getPeriodos());
        parquimetro = this.parquimetroRepository.save(parquimetro);

        return this.toParquimetroDTO(parquimetro);
    }

    private void encerraUltimoPeriodo(List<Periodo> periodos){
        Periodo ultimoPeriodo = periodoUtilService.ordenarDecrescentePegarPrimeiro(periodos)
                .orElseThrow(() -> new IllegalArgumentException(Utils.getMessage("periodo.nao.existe")));

        ultimoPeriodo.setAcaoPeriodo(AcaoPeriodo.ENCERRADO);
        this.periodoRepository.save(ultimoPeriodo);
    }

    private BigDecimal calcularValorTotalFixo(ParquimetroDTO parquimetroDTO){
        BigDecimal valorTotal = new BigDecimal("0.00");
        for (Periodo periodo : parquimetroDTO.periodos()) {
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
            periodo.setParquimetro(parquimetro);

            AcaoPeriodo acaoPeriodo = null;
            acaoPeriodo = AcaoPeriodo.ENCERRADO;

            periodo.setAcaoPeriodo(acaoPeriodo);
            periodos.add(periodo);
        }

        return periodos;
    }

    private ParquimetroDTO toParquimetroDTO(Parquimetro parquimetro) {
        return new ParquimetroDTO(
                parquimetro.getId(),
                parquimetro.getTipoParquimetro(),
                parquimetro.getVeiculo().getPlaca(),
                parquimetro.getCondutor().getCpf(),
                parquimetro.getLongitude(),
                parquimetro.getLatitude(),
                parquimetro.getValorHora(),
                parquimetro.getValorTotal(),
                parquimetro.getStatus(),
                parquimetro.getPeriodos()
        );
    }

    private Parquimetro toParquimetro(ParquimetroDTO parquimetroDTO) {
        return new Parquimetro(
                parquimetroDTO.tipoParquimetro(),
                parquimetroDTO.latitude(),
                parquimetroDTO.longitude(),
                parquimetroDTO.valorHora(),
                parquimetroDTO.valorTotal(),
                parquimetroDTO.status()
        );
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
}
