package com.fiap.parking.domain.schedule.impl;

import com.fiap.parking.domain.model.*;
import com.fiap.parking.domain.repositories.ParquimetroRepository;
import com.fiap.parking.domain.repositories.PeriodoRepository;
import com.fiap.parking.domain.schedule.MonitoramentoParquimetro;
import com.fiap.parking.domain.service.PeriodoUtilService;
import com.fiap.parking.infra.utils.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class MonitorarParquimetroVariavel implements MonitoramentoParquimetro {

    @Autowired
    private ParquimetroRepository parquimetroRepository;
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
        System.out.println("Iniciando Monitoramento Parquimetro Variavel [" + LocalDateTime.now() + "]");
        Optional<Parquimetro> parquimetroList = parquimetroRepository.findByStatusAndTipoParquimetro(
                StatusParquimetro.ABERTO, TipoParquimetro.VARIAVEL);
        List<Parquimetro> parquimetros =
                parquimetroList.map(Collections::singletonList).orElse(Collections.emptyList());

        //Integração com mensageria
        parquimetros.parallelStream().forEach(this::executar);

//        for(Parquimetro parquimetro : parquimetros){
//            this.executar(parquimetro);
//        }
    }

    private long calcularTempoPeriodo(Periodo ultimoPeriodo){
        return this.periodoUtilService.calcularIntervaloMinutos(ultimoPeriodo.getDataHoraFinal(),
                LocalDateTime.now());
    }

    private void executar(Parquimetro parquimetro){
        if (!parquimetro.getPeriodos().isEmpty()){
            final Periodo ultimoPeriodo = periodoUtilService.getDataFinalMaisRecenteDaListaDePeriodos(parquimetro.getPeriodos())
                    .orElseThrow(() -> new IllegalArgumentException(Utils.getMessage("excecao.periodo.nao.existe")));

            final long tempoCalculado = this.calcularTempoPeriodo(ultimoPeriodo);

            this.enviarNotificacao(tempoCalculado,ultimoPeriodo);
            this.saveProximoPeriodo(tempoCalculado,ultimoPeriodo,parquimetro);

            System.out.println(parquimetro);
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

            System.out.println("Notificacao para o parquimetro " + ultimoPeriodo.getParquimetro() + " enviada");
        }
    }

    private void saveProximoPeriodo(long tempo, Periodo ultimoPeriodo, Parquimetro parquimetro){
        //Sistema acescentara periodo apos "AGUARDA_RESPOSTA_USUARIO_MINUTE" tempo o usuario nao responder
        boolean resultado = tempo > this.AGUARDA_RESPOSTA_USUARIO_MINUTE;
        if (resultado){
            ultimoPeriodo.setAcaoPeriodo(AcaoPeriodo.RENOVADA);
            this.periodoRepository.save(ultimoPeriodo);
            this.periodoRepository.save(this.periodoUtilService.adicionaPeriodoVariavel(ultimoPeriodo.getDataHoraFinal(),parquimetro));
            //System.out.println("Parquimtro " + parquimetro.getId() + " acrescido mais tempo");
        }
    }
}
