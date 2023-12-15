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
public class MonitorarEstacionamentoVariavel implements MonitoramentoEstacionamento {

    @Autowired
    private EstacionamentoRepository estacionamentoRepository;
    @Autowired
    private PeriodoUtilService periodoUtilService;

    @Autowired
    private PeriodoRepository periodoRepository;
    private final long SEGUNDO = 1000 * 60 * 30; //30minutos
    private final int AGUARDA_RESPOSTA_USUARIO_MINUTE = 6;
    private final int NOTIFICACAO_TEMPO_INICIAL = -1;
    private final int NOTIFICACAO_TEMPO_FINAL = 5;
    @Scheduled(fixedRate = SEGUNDO)
    public void iniciarMonitoramento(){
        System.out.println("Iniciando Monitoramento Estacionamento Variavel [" + LocalDateTime.now() + "]");
        Optional<Estacionamento> estacionamentoList = estacionamentoRepository.findByStatusAndTipoTempo(
                StatusEstacionamento.ABERTO,TipoTempo.VARIAVEL);
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
            Periodo ultimoPeriodo = periodoUtilService.ordenarDecrescentePegarPrimeiro(estacionamento.getPeriodos())
                    .orElseThrow(() -> new IllegalArgumentException("Periodo não existe"));

            long tempoCalculado = this.calcularTempoPeriodo(ultimoPeriodo);

            this.enviarNotificacao(tempoCalculado,ultimoPeriodo);
            this.saveProximoPeriodo(tempoCalculado,ultimoPeriodo,estacionamento);
            //caso o usuario responder , a finalizacao sera no endpoint de resposta
        }
    }

    private void enviarNotificacao(long tempo, Periodo ultimoPeriodo){
        //Sistema enviara notificacao neste periodo de tempo APOS encerrar hora
        boolean resultado = tempo > this.NOTIFICACAO_TEMPO_INICIAL && tempo <= this.NOTIFICACAO_TEMPO_FINAL;
        if (resultado){
            ultimoPeriodo.setDataHoraNotificacao(LocalDateTime.now());
            ultimoPeriodo.setNotificacaoEnviada(NotificacaoEnviada.SIM);
            this.periodoRepository.save(ultimoPeriodo);

            System.out.println("Notificacao para o estacionamento " + ultimoPeriodo.getEstacionamento() + " enviada");
        }
    }

    private void saveProximoPeriodo(long tempo, Periodo ultimoPeriodo, Estacionamento estacionamento){
        //Sistema acescentara periodo apos "AGUARDA_RESPOSTA_USUARIO_MINUTE" tempo o usuario nao responder
        boolean resultado = tempo > this.AGUARDA_RESPOSTA_USUARIO_MINUTE;
        if (resultado){
            ultimoPeriodo.setAcaoPeriodo(AcaoPeriodo.RENOVADA);
            this.periodoRepository.save(ultimoPeriodo);
            this.periodoRepository.save(this.periodoUtilService.adicionaPeriodoVariavel(ultimoPeriodo.getDataHoraFinal(),estacionamento));
            //System.out.println("Estacionamento " + estacionamento.getId() + " acrescido mais tempo");
        }
    }
}
