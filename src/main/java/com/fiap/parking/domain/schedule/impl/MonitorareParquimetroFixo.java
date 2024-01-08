package com.fiap.parking.domain.schedule.impl;

import com.fiap.parking.domain.model.*;
import com.fiap.parking.domain.repositories.ParquimetroRepository;
import com.fiap.parking.domain.repositories.PeriodoRepository;
import com.fiap.parking.domain.schedule.MonitoramentoParquimetro;
import com.fiap.parking.domain.service.PeriodoUtilService;
import com.fiap.parking.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class MonitorareParquimetroFixo implements MonitoramentoParquimetro {
    @Autowired
    private ParquimetroRepository parquimetroRepository;
    @Autowired
    private PeriodoUtilService periodoUtilService;

    @Autowired
    private PeriodoRepository periodoRepository;
    private final long SEGUNDO = 1000 * 60 * 30; //30minutos
    private final int NOTIFICACAO_TEMPO = -5;
    private final int ENCERRA_PARQUIMETRO = 0;

    @Scheduled(fixedRate = SEGUNDO)
    public void iniciarMonitoramento(){

        System.out.println("Iniciando Monitoramento Parquimetro Fixo [" + LocalDateTime.now() + "]");
        Optional<Parquimetro> parquimetroList = parquimetroRepository.findByStatusAndTipoParquimetro(
                StatusParquimetro.ABERTO, TipoParquimetro.FIXO);
        List<Parquimetro> parquimetros = parquimetroList.map(Collections::singletonList).orElse(Collections.emptyList());

        for(Parquimetro parquimetro : parquimetros){
            this.executar(parquimetro);
        }
    }

    private long calcularTempoPeriodo(Periodo ultimoPeriodo){
        return this.periodoUtilService.calcularIntervaloMinutos(ultimoPeriodo.getDataHoraFinal(),
                LocalDateTime.now());
    }

    private void executar(Parquimetro parquimetro){
        if (!parquimetro.getPeriodos().isEmpty()){
            Periodo periodo = periodoUtilService.ordenarDecrescentePegarPrimeiro(parquimetro.getPeriodos())
                    .orElseThrow(() -> new IllegalArgumentException(Utils.getMessage("periodo.nao.existe")));

            long tempoCalculado = this.calcularTempoPeriodo(periodo);

            this.enviarNotificacao(tempoCalculado,periodo);
            this.encerrarParquimetro(tempoCalculado,parquimetro);
        }
    }

    private void enviarNotificacao(long tempo, Periodo ultimoPeriodo){
        //Sistema enviara notificacao neste periodo de tempo ANTES encerrar hora
        if (ultimoPeriodo.getNotificacaoEnviada() == null) {
            boolean resultado = tempo > this.NOTIFICACAO_TEMPO;
            if (resultado){
                ultimoPeriodo.setDataHoraNotificacao(LocalDateTime.now());
                ultimoPeriodo.setNotificacaoEnviada(NotificacaoEnviada.SIM);
                this.periodoRepository.save(ultimoPeriodo);

                System.out.println("Notificacao para o parquimtro " + ultimoPeriodo.getParquimetro() + " enviada");
            }
        }
    }

    private void encerrarParquimetro(long tempo, Parquimetro parquimetro){
        boolean resultado = tempo > this.ENCERRA_PARQUIMETRO;
        if (resultado) {
            parquimetro.setStatus(StatusParquimetro.ENCERRADO);
            parquimetroRepository.save(parquimetro);

            System.out.println("ParquimetroList " + parquimetro.getId() + " encerrado");
        }
    }
}
