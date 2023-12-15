package com.fiap.parking.domain.schedule.impl;

import com.fiap.parking.domain.model.*;
import com.fiap.parking.domain.repositories.EstacionamentoRepository;
import com.fiap.parking.domain.repositories.PeriodoRepository;
import com.fiap.parking.domain.schedule.MonitoramentoEstacionamento;
import com.fiap.parking.domain.service.PeriodoUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class MonitorareEstacionamentoFixo implements MonitoramentoEstacionamento {
    @Autowired
    private EstacionamentoRepository estacionamentoRepository;
    @Autowired
    private PeriodoUtilService periodoUtilService;

    @Autowired
    private PeriodoRepository periodoRepository;
    private final long SEGUNDO = 1000 * 60 * 30; //30minutos
    private final int NOTIFICACAO_TEMPO = -5;
    private final int ENCERRA_ESTACIONAMENTO = 0;

    @Scheduled(fixedRate = SEGUNDO)
    public void iniciarMonitoramento(){

        System.out.println("Iniciando Monitoramento Estacionamento Fixo [" + LocalDateTime.now() + "]");
        Optional<Estacionamento> estacionamentoList = estacionamentoRepository.findByStatusAndTipoTempo(
                StatusEstacionamento.ABERTO,TipoTempo.FIXO);
        List<Estacionamento> estacionamentos = estacionamentoList.map(Collections::singletonList).orElse(Collections.emptyList());

        for(Estacionamento estacionamento : estacionamentos){
            this.executar(estacionamento);
        }
    }

    private long calcularTempoPeriodo(Periodo ultimoPeriodo){
        return this.periodoUtilService.calcularIntervaloMinutos(ultimoPeriodo.getDataHoraFinal(),
                LocalDateTime.now());
    }

    private void executar(Estacionamento estacionamento){
        if (!estacionamento.getPeriodos().isEmpty()){
            Periodo periodo = periodoUtilService.ordenarDecrescentePegarPrimeiro(estacionamento.getPeriodos())
                    .orElseThrow(() -> new IllegalArgumentException("Periodo não existe"));

            long tempoCalculado = this.calcularTempoPeriodo(periodo);

            this.enviarNotificacao(tempoCalculado,periodo);
            this.encerrarEstacionamento(tempoCalculado,estacionamento);
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

                System.out.println("Notificacao para o estacionamento " + ultimoPeriodo.getEstacionamento() + " enviada");
            }
        }
    }

    private void encerrarEstacionamento(long tempo, Estacionamento estacionamento){
        boolean resultado = tempo > this.ENCERRA_ESTACIONAMENTO;
        if (resultado) {
            estacionamento.setStatus(StatusEstacionamento.ENCERRADO);
            estacionamentoRepository.save(estacionamento);

            System.out.println("Estacionamento " + estacionamento.getId() + " encerrado");
        }
    }
}
