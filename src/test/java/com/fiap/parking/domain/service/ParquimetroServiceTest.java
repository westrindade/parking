package com.fiap.parking.domain.service;

import com.fiap.parking.domain.dto.*;
import com.fiap.parking.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.fiap.parking.domain.exception.EntidadeNaoEncontrada;
import com.fiap.parking.domain.repositories.ParquimetroRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class ParquimetroServiceTest {

    @Mock
    private ParquimetroRepository parquimetroRepository;
    @Mock
    private PeriodoUtilService periodoUtilService;
    @Mock
    private PeriodoService periodoService;
    @Mock
    private VeiculoService veiculoService;
    @Mock
    private CondutorService condutorService;

    @InjectMocks
    private ParquimetroService parquimetroService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Parquimetro criarParquimetroComDadosBasicos() {
        Parquimetro parquimetro = new Parquimetro();
        parquimetro.setId(UUID.randomUUID());
        // Suponha que Veiculo e Condutor são classes já definidas no seu domínio
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca("ABC1234");
        parquimetro.setVeiculo(veiculo);
        Condutor condutor = new Condutor();
        condutor.setCpf("123.456.789-00");
        parquimetro.setCondutor(condutor);
        return parquimetro;
    }

    private List<Periodo> criarPeriodosFixos() {
        List<Periodo> periodos = new ArrayList<>();
        Periodo periodo = new Periodo();
        // o período fixo começa como ENCERRADO
        periodo.setAcaoPeriodo(AcaoPeriodo.ENCERRADO);
        periodo.setDataHoraInicial(LocalDateTime.now());
        periodo.setDataHoraFinal(LocalDateTime.now().plusHours(1)); // supondo um período de 1 hora
        periodos.add(periodo);
        return periodos;
    }

    @Test
    public void deveAdicionarPeriodoFixoAoParquimetro() {
        // Arrange
        Parquimetro parquimetro = criarParquimetroComDadosBasicos();
        parquimetro.setPeriodos(criarPeriodosFixos());

        // Act
        // Suponhamos que o método adicionarPeriodoFixo configura todos os períodos como ENCERRADO
        List<Periodo> periodos = parquimetroService.adicionarPeriodoFixo(parquimetro);

        // Assert
        assertFalse(periodos.isEmpty(), "A lista de períodos não deve estar vazia.");
        assertTrue(periodos.stream().allMatch(p -> p.getAcaoPeriodo() == AcaoPeriodo.ENCERRADO),
                "Todos os períodos devem ter a ação ENCERRADO.");
    }

    @Test
    public void deveEncerrarParquimetroQuandoCondutorInformaRespostaComIdValido() {
        // Arrange
        UUID idValido = UUID.randomUUID();
        Parquimetro parquimetroEncontrado = new Parquimetro();
        parquimetroEncontrado.setId(idValido);
        parquimetroEncontrado.setTipoParquimetro(TipoParquimetro.VARIAVEL);
        parquimetroEncontrado.setStatus(StatusParquimetro.ABERTO);

        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca("ABC1234");
        parquimetroEncontrado.setVeiculo(veiculo);

        Condutor condutor = new Condutor();
        condutor.setCpf("123.456.789-00");
        parquimetroEncontrado.setCondutor(condutor);

        List<Periodo> periodos = new ArrayList<>();
        Periodo periodo = new Periodo();
        periodos.add(periodo);
        parquimetroEncontrado.setPeriodos(periodos);

        when(parquimetroRepository.findById(idValido)).thenReturn(Optional.of(parquimetroEncontrado));
        when(parquimetroRepository.save(any(Parquimetro.class))).thenReturn(parquimetroEncontrado);
        when(veiculoService.findById(anyString())).thenReturn(veiculo.toDTO());
        when(condutorService.findByCpf(anyString())).thenReturn(condutor.toDTO()); // Mock a chamada ao condutorService
        when(periodoUtilService.getDataFinalMaisRecenteDaListaDePeriodos(any())).thenReturn(Optional.of(periodo));

        // Act
        ParquimetroDTO resultado = parquimetroService.condutorInformaResposta(idValido);

        // Assert
        assertEquals(StatusParquimetro.ENCERRADO, resultado.getStatus());
        verify(parquimetroRepository).save(parquimetroEncontrado);
        verify(periodoService).save(periodo); // Verifica se o método save foi chamado com o período esperado
    }


    @Test
    public void deveLancarExcecaoQuandoCondutorInformaRespostaComIdInvalido() {
        // Arrange
        UUID idInvalido = UUID.randomUUID();
        when(parquimetroRepository.findById(idInvalido)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntidadeNaoEncontrada.class, () -> parquimetroService.condutorInformaResposta(idInvalido));
    }

    @Test
    public void deveEncerrarUltimoPeriodoQuandoCondutorInformaResposta() {
        // Arrange
        UUID idValido = UUID.randomUUID();
        Parquimetro parquimetro = new Parquimetro();
        parquimetro.setId(idValido);
        parquimetro.setTipoParquimetro(TipoParquimetro.VARIAVEL);
        parquimetro.setStatus(StatusParquimetro.ABERTO);

        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca("ABC1234");
        parquimetro.setVeiculo(veiculo);

        Condutor condutor = new Condutor();
        condutor.setCpf("123.456.789-00");
        parquimetro.setCondutor(condutor);

        List<Periodo> periodos = new ArrayList<>();
        Periodo ultimoPeriodo = new Periodo();
        periodos.add(ultimoPeriodo);
        parquimetro.setPeriodos(periodos);

        when(parquimetroRepository.findById(idValido)).thenReturn(Optional.of(parquimetro));
        when(parquimetroRepository.save(any(Parquimetro.class))).thenReturn(parquimetro);
        when(periodoUtilService.getDataFinalMaisRecenteDaListaDePeriodos(periodos)).thenReturn(Optional.of(ultimoPeriodo));

        // Act
        parquimetroService.condutorInformaResposta(idValido);

        // Assert
        verify(periodoUtilService).getDataFinalMaisRecenteDaListaDePeriodos(periodos);
        assertEquals(AcaoPeriodo.ENCERRADO, ultimoPeriodo.getAcaoPeriodo());
        verify(periodoService).save(ultimoPeriodo);
    }

    @Test
    public void deveCalcularValorTotalFixoCorretamente() {
        // Arrange
        Parquimetro parquimetro = new Parquimetro();
        List<Periodo> periodos = new ArrayList<>();

        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fim = inicio.plusHours(2); // 2 horas de diferença
        Periodo periodo = new Periodo();
        periodo.setDataHoraInicial(inicio);
        periodo.setDataHoraFinal(fim);
        periodos.add(periodo);

        parquimetro.setPeriodos(periodos);

        // Valor fixo conhecido
        BigDecimal valorHora = BigDecimal.valueOf(8.90);
        when(periodoUtilService.calcularIntervaloHoras(inicio, fim)).thenReturn(2L); // 2 horas

        // Act
        BigDecimal valorTotal = parquimetroService.calcularValorTotalFixo(parquimetro);

        // Assert
        BigDecimal valorEsperado = valorHora.multiply(BigDecimal.valueOf(2)); // 8.90 * 2
        assertEquals(0, valorEsperado.compareTo(valorTotal), "O valor total calculado deve ser igual ao valor esperado.");
    }

    @Test
    public void deveCalcularValorTotalVariavelCorretamente() {
        // Arrange
        UUID idValido = UUID.randomUUID();
        Parquimetro parquimetro = new Parquimetro();
        parquimetro.setId(idValido);
        parquimetro.setTipoParquimetro(TipoParquimetro.VARIAVEL);
        parquimetro.setStatus(StatusParquimetro.ABERTO);

        // Configura um Veículo válido para o Parquimetro
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca("ABC1234");
        parquimetro.setVeiculo(veiculo);

        // Configura um Condutor válido para o Parquimetro
        Condutor condutor = new Condutor();
        condutor.setCpf("123.456.789-00");
        parquimetro.setCondutor(condutor);

        // Adiciona períodos ao Parquimetro
        List<Periodo> periodos = new ArrayList<>();
        periodos.add(new Periodo());
        periodos.add(new Periodo());
        parquimetro.setPeriodos(periodos);

        when(parquimetroRepository.findById(idValido)).thenReturn(Optional.of(parquimetro));
        when(parquimetroRepository.save(any(Parquimetro.class))).thenReturn(parquimetro);
        when(periodoUtilService.getDataFinalMaisRecenteDaListaDePeriodos(periodos)).thenReturn(Optional.of(new Periodo()));

        // O valor da hora é configurado internamente no ParquimetroService e é conhecido (8.90)
        BigDecimal valorEsperado = BigDecimal.valueOf(8.90).multiply(new BigDecimal(periodos.size())); // 8.90 * número de períodos

        // Act
        ParquimetroDTO resultado = parquimetroService.condutorInformaResposta(idValido);

        // Assert
        assertEquals(valorEsperado, resultado.getValorTotal());
    }

    @Test
    public void testAdicionarPeriodoFixo() {
        // Preparação
        Parquimetro parquimetro = new Parquimetro();
        List<Periodo> periodosOriginais = new ArrayList<>();
        Periodo periodo = new Periodo();
        periodo.setDataHoraInicial(LocalDateTime.now()); // Configura a data e hora iniciais
        periodo.setDataHoraFinal(LocalDateTime.now().plusHours(1)); // Configura a data e hora finais
        periodo.setAcaoPeriodo(AcaoPeriodo.ENCERRADO); // Configura a ação do período para ENCERRADO
        periodosOriginais.add(periodo);
        parquimetro.setPeriodos(periodosOriginais);

        // Ação
        List<Periodo> periodosResultantes = parquimetroService.adicionarPeriodoFixo(parquimetro);

        // Verificações
        assertNotNull(periodosResultantes);
        assertFalse(periodosResultantes.isEmpty());
        assertEquals(periodosOriginais.size(), periodosResultantes.size());
        for (Periodo p : periodosResultantes) {
            assertEquals(AcaoPeriodo.ENCERRADO, p.getAcaoPeriodo());
        }

        // Verifica se não há efeitos colaterais indesejados
        assertEquals(periodosOriginais.size(), parquimetro.getPeriodos().size());
        for (int i = 0; i < periodosOriginais.size(); i++) {
            assertSame(periodosOriginais.get(i), periodosResultantes.get(i));
        }

        // Verifica se nenhum método indesejado foi chamado nas dependências
        verify(parquimetroRepository, never()).save(any(Parquimetro.class));
    }

    @Test
    public void testFindByCondutor() {
        // Preparação
        String cpfTeste = "12345678900";

        // Criação de Veículos e Condutores
        VeiculoDTO veiculoDTO1 = new VeiculoDTO("PLACA1234", "Modelo 1", "Cor 1");
        Veiculo veiculo1 = veiculoDTO1.toVeiculo();
        VeiculoDTO veiculoDTO2 = new VeiculoDTO("PLACA5678", "Modelo 2", "Cor 2");
        Veiculo veiculo2 = veiculoDTO2.toVeiculo();
        List<VeiculoDTO> veiculosDTO1 = Arrays.asList(veiculoDTO1);
        CondutorDTO condutorDTO1 = new CondutorDTO("12345678900", "Condutor 1", "11987654321", LocalDate.of(1990, 1, 1),
                "Tipo Logradouro", "Logradouro", "Nº 100", "Bairro", "Cidade", "UF",
                "12345-678", TipoPagamento.PIX, veiculosDTO1);
        Condutor condutor1 = condutorDTO1.toCondutor();
        List<VeiculoDTO> veiculosDTO2 = Arrays.asList(veiculoDTO2);
        CondutorDTO condutorDTO2 = new CondutorDTO("98765432100", "Condutor 2", "11987654321", LocalDate.of(1990, 1, 1),
                "Tipo Logradouro", "Logradouro", "Nº 200", "Bairro", "Cidade", "UF",
                "87654-321", TipoPagamento.PIX, veiculosDTO2);
        Condutor condutor2 = condutorDTO2.toCondutor();

        // Configuração de Parquímetros
        Parquimetro parquimetro1 = new Parquimetro();
        parquimetro1.setId(UUID.randomUUID());
        parquimetro1.setCondutor(condutor1);
        parquimetro1.setVeiculo(veiculo1);
        parquimetro1.setTipoParquimetro(TipoParquimetro.FIXO);
        parquimetro1.setLongitude("123.456");
        parquimetro1.setLatitude("78.901");
        parquimetro1.setValorHora(new BigDecimal("10.00"));
        parquimetro1.setValorTotal(new BigDecimal("100.00"));
        parquimetro1.setStatus(StatusParquimetro.ABERTO);
        parquimetro1.setPeriodos(new ArrayList<>());

        Parquimetro parquimetro2 = new Parquimetro();
        parquimetro2.setId(UUID.randomUUID());
        parquimetro2.setCondutor(condutor2);
        parquimetro2.setVeiculo(veiculo2);
        parquimetro2.setTipoParquimetro(TipoParquimetro.VARIAVEL);
        parquimetro2.setLongitude("654.321");
        parquimetro2.setLatitude("98.765");
        parquimetro2.setValorHora(new BigDecimal("15.00"));
        parquimetro2.setValorTotal(new BigDecimal("150.00"));
        parquimetro2.setStatus(StatusParquimetro.ENCERRADO);
        parquimetro2.setPeriodos(new ArrayList<>());

        when(parquimetroRepository.findByCondutor(cpfTeste)).thenReturn(Arrays.asList(parquimetro1, parquimetro2));

        // Ação
        List<ParquimetroDTO> resultado = parquimetroService.findByCondutor(cpfTeste);

        // Verificações
        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        ParquimetroDTO dtoResultado1 = resultado.get(0);
        assertEquals(parquimetro1.getId(), dtoResultado1.getId());
        assertEquals(parquimetro1.getVeiculo().getPlaca(), dtoResultado1.getVeiculo());
        assertEquals(parquimetro1.getCondutor().getCpf(), dtoResultado1.getCondutor());
        assertEquals(parquimetro1.getLongitude(), dtoResultado1.getLongitude());
        assertEquals(parquimetro1.getLatitude(), dtoResultado1.getLatitude());

        ParquimetroDTO dtoResultado2 = resultado.get(1);
        assertEquals(parquimetro2.getId(), dtoResultado2.getId());
        assertEquals(parquimetro2.getVeiculo().getPlaca(), dtoResultado2.getVeiculo());
        assertEquals(parquimetro2.getCondutor().getCpf(), dtoResultado2.getCondutor());
        assertEquals(parquimetro2.getLongitude(), dtoResultado2.getLongitude());
        assertEquals(parquimetro2.getLatitude(), dtoResultado2.getLatitude());

        verify(condutorService).findByCpf(cpfTeste);
        verify(parquimetroRepository).findByCondutor(cpfTeste);
    }

    @Test
    public void testFindByCondutorAndStatus() {
        // Preparação
        String cpfTeste = "12345678900";
        StatusParquimetro statusTeste = StatusParquimetro.ABERTO;

        UUID parquimetroId1 = UUID.randomUUID();
        UUID parquimetroId2 = UUID.randomUUID();
        PeriodoDTO periodoDTO1 = new PeriodoDTO(parquimetroId1, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        PeriodoDTO periodoDTO2 = new PeriodoDTO(parquimetroId2, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));

        ParquimetroDTO dtoMock1 = new ParquimetroFixoDTO(parquimetroId1, "PLACA1", cpfTeste, "123.456", "78.901", new BigDecimal("10.00"), new BigDecimal("100.00"), StatusParquimetro.ABERTO, Arrays.asList(periodoDTO1), TipoParquimetro.FIXO);
        ParquimetroDTO dtoMock2 = new ParquimetroVariavelDTO(parquimetroId2, "PLACA2", cpfTeste, "654.321", "98.765", new BigDecimal("15.00"), new BigDecimal("150.00"), StatusParquimetro.ENCERRADO, Arrays.asList(periodoDTO2), TipoParquimetro.VARIAVEL);

        // Configuração dos mocks
        Parquimetro parquimetroMock1 = mock(Parquimetro.class);
        Parquimetro parquimetroMock2 = mock(Parquimetro.class);
        when(parquimetroMock1.toDTO()).thenReturn(dtoMock1);
        when(parquimetroMock2.toDTO()).thenReturn(dtoMock2);
        when(parquimetroRepository.findByCondutorAndStatus(cpfTeste, statusTeste)).thenReturn(Arrays.asList(parquimetroMock1, parquimetroMock2));

        // Ação
        List<ParquimetroDTO> resultado = parquimetroService.findByCondutorAndStatus(cpfTeste, statusTeste);

        // Verificações
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertSame(dtoMock1, resultado.get(0));
        assertSame(dtoMock2, resultado.get(1));

        verify(condutorService).findByCpf(cpfTeste);
        verify(parquimetroRepository).findByCondutorAndStatus(cpfTeste, statusTeste);
    }

    // Classes fictícias para simular subclasses concretas de ParquimetroDTO
    private static class ParquimetroFixoDTO extends ParquimetroDTO {
        public ParquimetroFixoDTO(UUID id, String veiculo, String condutor, String longitude, String latitude, BigDecimal valorHora, BigDecimal valorTotal, StatusParquimetro status, List<PeriodoDTO> periodos, TipoParquimetro tipoParquimetro) {
            super(id, veiculo, condutor, longitude, latitude, valorHora, valorTotal, status, periodos, tipoParquimetro);
        }
    }

    private static class ParquimetroVariavelDTO extends ParquimetroDTO {
        public ParquimetroVariavelDTO(UUID id, String veiculo, String condutor, String longitude, String latitude, BigDecimal valorHora, BigDecimal valorTotal, StatusParquimetro status, List<PeriodoDTO> periodos, TipoParquimetro tipoParquimetro) {
            super(id, veiculo, condutor, longitude, latitude, valorHora, valorTotal, status, periodos, tipoParquimetro);
        }
    }
}
