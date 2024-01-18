package com.fiap.parking.domain.service;

import com.fiap.parking.domain.dto.ParquimetroDTO;
import com.fiap.parking.domain.exception.EntidadeNaoEncontrada;
import com.fiap.parking.domain.model.*;
import com.fiap.parking.domain.repositories.ParquimetroRepository;
import com.fiap.parking.domain.service.ParquimetroService;
import com.fiap.parking.infra.utils.Utils;

import jakarta.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ParquimetroService {

    private ParquimetroRepository parquimetroRepository;
    private PeriodoUtilService periodoUtilService;
    private PeriodoService periodoService;
    private VeiculoService veiculoService;
    private CondutorService condutorService;

    private BigDecimal valorHora = BigDecimal.valueOf(8.90);

    public ParquimetroService(
		@Autowired ParquimetroRepository parquimetroRepository, 
		@Autowired PeriodoUtilService periodoUtilService,
		@Autowired PeriodoService periodoService, 
		@Autowired VeiculoService veiculoService, 
		@Autowired CondutorService condutorService
	) {
		this.parquimetroRepository = parquimetroRepository;
		this.periodoUtilService = periodoUtilService;
		this.periodoService = periodoService;
		this.veiculoService = veiculoService;
		this.condutorService = condutorService;
	}

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
    
    public ParquimetroDTO save(@NotNull final Parquimetro parquimetro){
    	if(parquimetro.getTipoParquimetro() == null) {
    		throw new IllegalArgumentException(Utils.getMessage("excecao.parquimetro.nao.encontrado"));
    	}
    	
        var veiculo = this.veiculoService.findById(parquimetro.getVeiculo().getPlaca());
        var condutor = this.condutorService.findByCpf(parquimetro.getCondutor().getCpf());

        parquimetro.setValorHora(this.valorHora);
        parquimetro.setStatus(StatusParquimetro.ABERTO);

        if (TipoParquimetro.FIXO == parquimetro.getTipoParquimetro()){
        	parquimetro.encerrarTodosPeriodos();
            parquimetro.setValorTotal(this.calcularValorTotalFixo(parquimetro));
            
            if (parquimetro.getPeriodos().isEmpty()) {
            	throw new IllegalArgumentException(Utils.getMessage("excecao.periodo.nao.informado"));
        	}
        } 
        
        if (TipoParquimetro.VARIAVEL == parquimetro.getTipoParquimetro()) {
            parquimetro.getPeriodos().add(this.periodoUtilService.adicionaPeriodoVariavel(LocalDateTime.now(), parquimetro));
        }


        parquimetro.setCondutor(condutor.toCondutor());
        parquimetro.setVeiculo(veiculo.toVeiculo());

        return this.parquimetroRepository.save(parquimetro).toDTO();
    }

    public ParquimetroDTO condutorInformaResposta(UUID id){
        Parquimetro parquimetro = this.parquimetroRepository.findById(id)
                                            .orElseThrow( () -> new EntidadeNaoEncontrada("excecao.parquimetro.nao.encontrado") );
        parquimetro.setStatus(StatusParquimetro.ENCERRADO);
        if (parquimetro.getTipoParquimetro() == TipoParquimetro.VARIAVEL) {
            parquimetro.setValorTotal(this.calcularValorTotalVariavel(parquimetro));
        }
        this.encerraUltimoPeriodo(parquimetro.getPeriodos());
        parquimetro = this.parquimetroRepository.save(parquimetro);

        return parquimetro.toDTO();
    }

    private void encerraUltimoPeriodo(List<Periodo> periodos){
        final Optional<Periodo> ultimoPeriodoOpt = periodoUtilService.getDataFinalMaisRecenteDaListaDePeriodos(periodos);
        final Periodo ultimoPeriodo = ultimoPeriodoOpt.orElseThrow(() -> new EntidadeNaoEncontrada("excecao.periodo.nao.existe"));

        ultimoPeriodo.setAcaoPeriodo(AcaoPeriodo.ENCERRADO);
        this.periodoService.save(ultimoPeriodo);
    }

    private BigDecimal calcularValorTotalFixo(Parquimetro parquimetro){
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (Periodo periodo : parquimetro.getPeriodos()) {
            BigDecimal calcularIntervaloHoras = this.periodoUtilService.calcularIntervaloHoras(periodo.getDataHoraInicial(),periodo.getDataHoraFinal());
			valorTotal = this.valorHora.multiply(calcularIntervaloHoras);
        }
        return valorTotal;
    }

    private BigDecimal calcularValorTotalVariavel(Parquimetro parquimetro){
        int totalPeriodos = parquimetro.getPeriodos().size();
        return this.valorHora.multiply(new BigDecimal(totalPeriodos));
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