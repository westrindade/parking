package com.fiap.parking.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.fiap.parking.domain.model.Parquimetro;
import com.fiap.parking.domain.model.Periodo;
import com.fiap.parking.infra.utils.Utils;

@SpringBootTest
public class PeriodoUtilServiceTest {
  
    private static final String PARAMETRO_DATA_FINAL_OBRIGATORIO = "parametro.data.final.obrigatorio";
    private static final String PARAMETRO_DATA_INICIO_OBRIGATORIO = "parametro.data.inicio.obrigatorio";
  
    @Autowired
    PeriodoUtilService periodoUtilService;

    @Test
    void deveDispararExcessaoCasoParametrosObriatoriosSejamNullParaAdicionarPeriodoVariavel(){
       final IllegalArgumentException excessaoDataUltimoPeriodo = assertThrows(IllegalArgumentException.class, () ->  periodoUtilService.adicionaPeriodoVariavel(null, null));
       final IllegalArgumentException excessaoParquimetro = assertThrows(IllegalArgumentException.class, () ->  periodoUtilService.adicionaPeriodoVariavel(LocalDateTime.now(), null));
    
        assertEquals(Utils.getMessage("parametro.data.ultimo.periodo.obrigatorio"), excessaoDataUltimoPeriodo.getMessage());
        assertEquals(Utils.getMessage("parametro.parquimetro.obrigatorio"), excessaoParquimetro.getMessage());
    }

    @Test
    void deveDevolverPeriodoComDataInicialFinalPreenchidasParaAdicionarPeriodoVariavel(){
        final Periodo periodo =  periodoUtilService.adicionaPeriodoVariavel(LocalDateTime.now(), new Parquimetro());
        
        assertNotNull(periodo.getParquimetro());
        assertNotNull(periodo.getDataHoraInicial());
        assertNotNull(periodo.getDataHoraFinal());
    }

    @Test
    void deveDispararExcessaoCasoParametroObrigatorioSejaNullParaGetDataFinalMaisRecenteDaListaDePeriodos(){
        final IllegalArgumentException excessao = assertThrows(IllegalArgumentException.class, () ->  periodoUtilService.getDataFinalMaisRecenteDaListaDePeriodos(null));
       
         assertEquals(Utils.getMessage("parametro.lista.periodos.obrigatorio"), excessao.getMessage());
    }

    @Test
    void deveRetornarOptionalVazioCasoNemPeriodoSejaPassadoNoParametroParaGetDataFinalMaisRecenteDaListaDePeriodos(){
        final Optional<Periodo> opt = periodoUtilService.getDataFinalMaisRecenteDaListaDePeriodos(Collections.emptyList());

        assertTrue(opt.isEmpty());
    }

    @Test 
    void deveRecuperarPeriodoMaisRecenteParaGetDataFinalMaisRecenteDaListaDePeriodos(){
        final Periodo periodo1 = Periodo.builder().dataHoraFinal(LocalDateTime.now()).build();
        final Periodo periodo2 = Periodo.builder().dataHoraFinal(LocalDateTime.now().plusHours(1)).build();
        final Periodo periodo3 = Periodo.builder().dataHoraFinal(LocalDateTime.now().minusDays(1)).build();

        final List<Periodo> asList = java.util.Arrays.asList(periodo1, periodo2, periodo3);
        Collections.shuffle(asList);
        
        final Optional<Periodo> opt = periodoUtilService.getDataFinalMaisRecenteDaListaDePeriodos(asList);

        assertEquals(periodo2, opt.get());
    }

    @Test
    void deveDispararExcessaoCasoParametrosObriatoriosSejamNullParaCalcularIntervaloHoras(){
       final IllegalArgumentException excessaoDataInicio = assertThrows(IllegalArgumentException.class, () ->  periodoUtilService.calcularIntervaloHoras(null, null));
       final IllegalArgumentException excessaoDataFinal = assertThrows(IllegalArgumentException.class, () ->  periodoUtilService.calcularIntervaloHoras(LocalDateTime.now(), null));
    
       assertEquals(Utils.getMessage(PARAMETRO_DATA_INICIO_OBRIGATORIO), excessaoDataInicio.getMessage());
       assertEquals(Utils.getMessage(PARAMETRO_DATA_FINAL_OBRIGATORIO), excessaoDataFinal.getMessage());
    }

    @Test
    void deveRetornarUmCasoIntervaloSejaInferioraUmaHoraParaCalcularIntervaloHoras(){
        final LocalDateTime dataInicial = LocalDateTime.now();
        final LocalDateTime dataFinal = dataInicial.plusMinutes(30);
        final BigDecimal intervaloEmHoras = periodoUtilService.calcularIntervaloHoras(dataInicial, dataFinal);

        final BigDecimal umaHora = BigDecimal.valueOf(1l);
        assertEquals(umaHora, intervaloEmHoras);
    }

    @Test
    void deveRetornarHorasCasoIntervaloSejaSuperiorUmaHoraParaCalcularIntervaloHoras(){
        final LocalDateTime dataInicial = LocalDateTime.now();
        final LocalDateTime dataFinal = dataInicial.plusHours(2);
        final BigDecimal intervaloEmHoras = periodoUtilService.calcularIntervaloHoras(dataInicial, dataFinal);

        final BigDecimal duasHoras = BigDecimal.valueOf(2l);
        assertEquals(duasHoras, intervaloEmHoras);
    }

    @Test
    void deveDispararExcessaoCasoParametrosObriatoriosSejamNullParaCalcularIntervaloMinutos(){
       final IllegalArgumentException excessaoDataInicio = assertThrows(IllegalArgumentException.class, () ->  periodoUtilService.calcularIntervaloMinutos(null, null));
       final IllegalArgumentException excessaoDataFinal = assertThrows(IllegalArgumentException.class, () ->  periodoUtilService.calcularIntervaloMinutos(LocalDateTime.now(), null));
    
       assertEquals(Utils.getMessage(PARAMETRO_DATA_INICIO_OBRIGATORIO), excessaoDataInicio.getMessage());
       assertEquals(Utils.getMessage(PARAMETRO_DATA_FINAL_OBRIGATORIO), excessaoDataFinal.getMessage());
    }

    @Test
    void deveRetornarMinutosCasoIntervaloSejaSuperiorUmaHoraParaCalcularIntervaloMinutos(){
        final LocalDateTime dataInicial = LocalDateTime.now();
        final LocalDateTime dataFinal = dataInicial.plusHours(1);
        final long intervaloEmMinutos = periodoUtilService.calcularIntervaloMinutos(dataInicial, dataFinal);

        final long sessentaMinutos = 60l;
        assertEquals(sessentaMinutos, intervaloEmMinutos);
    }

}
