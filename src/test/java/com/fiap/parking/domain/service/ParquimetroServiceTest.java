package com.fiap.parking.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.fiap.parking.domain.dto.CondutorDTO;
import com.fiap.parking.domain.dto.ParquimetroDTO;
import com.fiap.parking.domain.dto.ParquimetroFixoDTO;
import com.fiap.parking.domain.dto.ParquimetroVariavelDTO;
import com.fiap.parking.domain.dto.PeriodoDTO;
import com.fiap.parking.domain.dto.VeiculoDTO;
import com.fiap.parking.domain.exception.EntidadeNaoEncontrada;
import com.fiap.parking.domain.model.AcaoPeriodo;
import com.fiap.parking.domain.model.Condutor;
import com.fiap.parking.domain.model.Parquimetro;
import com.fiap.parking.domain.model.Periodo;
import com.fiap.parking.domain.model.StatusParquimetro;
import com.fiap.parking.domain.model.TipoPagamento;
import com.fiap.parking.domain.model.TipoParquimetro;
import com.fiap.parking.domain.model.Veiculo;
import com.fiap.parking.domain.repositories.ParquimetroRepository;

public class ParquimetroServiceTest {

    @Mock
    private ParquimetroRepository parquimetroRepository;
    @Spy
    private PeriodoUtilService periodoUtilService;
    @Mock
    private PeriodoService periodoService;
    @Mock
    private VeiculoService veiculoService;
    @Mock
    private CondutorService condutorService;

    @InjectMocks
    private ParquimetroService parquimetroService;
    private AutoCloseable autoCloseable;

    @BeforeEach
    public void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }
    @AfterEach
    public void setDown() throws Exception {
    	autoCloseable.close();
    	
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

        // Act
        ParquimetroDTO resultado = parquimetroService.condutorInformaResposta(idValido);

        // Assert
        assertEquals(StatusParquimetro.ENCERRADO, resultado.getStatus());
        verify(parquimetroRepository).save(parquimetroEncontrado);
        
        when(parquimetroRepository.findById(idValido)).thenReturn(
        		Optional.of(parquimetroEncontrado
        				.toBuilder()
        				.tipoParquimetro(TipoParquimetro.FIXO)
        				.status(StatusParquimetro.ABERTO)
        				.build()
				));
        resultado = parquimetroService.condutorInformaResposta(idValido);
        
        assertEquals(StatusParquimetro.ENCERRADO, resultado.getStatus());
        verify(parquimetroRepository).save(parquimetroEncontrado);
        verify(periodoService, times(2)).save(periodo);
    }


    @Test
    public void deveLancarExcecaoQuandoCondutorInformaRespostaComIdInvalido() {
        // Arrange
        UUID idInvalido = UUID.randomUUID();
        when(parquimetroRepository.findById(idInvalido)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntidadeNaoEncontrada.class, () -> parquimetroService.condutorInformaResposta(idInvalido));
        
        when(parquimetroRepository.findById(idInvalido)).thenReturn(Optional.of(Parquimetro.builder().periodos(Collections.emptyList()).build()));
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
        periodos.add(Periodo.builder().dataHoraFinal(LocalDateTime.now()).build());
        periodos.add(Periodo.builder().dataHoraFinal(LocalDateTime.now().plusHours(3)).build());
        parquimetro.setPeriodos(periodos);

        when(parquimetroRepository.findById(idValido)).thenReturn(Optional.of(parquimetro));
        when(parquimetroRepository.save(any(Parquimetro.class))).thenReturn(parquimetro);

        // O valor da hora é configurado internamente no ParquimetroService e é conhecido (8.90)
        BigDecimal valorEsperado = BigDecimal.valueOf(8.90).multiply(new BigDecimal(periodos.size())); // 8.90 * número de períodos

        // Act
        ParquimetroDTO resultado = parquimetroService.condutorInformaResposta(idValido);

        // Assert
        assertEquals(valorEsperado, resultado.getValorTotal());
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

        ParquimetroDTO dtoMock1 = new ParquimetroFixoDTO(parquimetroId1, "PLACA1", cpfTeste, "123.456", "78.901", new BigDecimal("10.00"), new BigDecimal("100.00"), StatusParquimetro.ABERTO, Arrays.asList(periodoDTO1));
        ParquimetroDTO dtoMock2 = new ParquimetroVariavelDTO(parquimetroId2, "PLACA2", cpfTeste, "654.321", "98.765", new BigDecimal("15.00"), new BigDecimal("150.00"), StatusParquimetro.ENCERRADO, Arrays.asList(periodoDTO2));

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

    @Test
    public void naoDeveSalvarParquimetroQuandoTipoParquimetroNaoForInformado() {
		assertThrowsExactly(IllegalArgumentException.class, () -> parquimetroService.save(Parquimetro.builder().build()));
    }
    
    @Test
    public void naoDeveSalvarParquimetroFixoQuandoPeriodoNaoForInformado() {
    	final Veiculo veiculo = new Veiculo("ABC-1234", "FUSCA", "VERDE");
    	
    	final Condutor condutor = Condutor
			.builder()
				.cpf("12345678900")
			.build(); 
    	
    	final Parquimetro fixo = Parquimetro
			.builder()
				.tipoParquimetro(TipoParquimetro.FIXO)
				.condutor(condutor)
				.veiculo(veiculo)
			.build(); 
    	
		when(veiculoService.findById(anyString())).thenReturn(veiculo.toDTO());
		
		when(condutorService.findByCpf(anyString())).thenReturn(condutor.toDTO());
    	
		assertThrowsExactly(IllegalArgumentException.class, () -> parquimetroService.save(fixo));
    }
    
    @Test
    public void deveSalvarParquimetroFixoQuandoPeriodoInformado() {
    	final Periodo periodo1 = Periodo.builder().dataHoraInicial(LocalDateTime.now()).dataHoraFinal(LocalDateTime.now().plusHours(5)).build();
    	final Periodo periodo2 = Periodo.builder().dataHoraInicial(LocalDateTime.now().plusHours(1)).dataHoraFinal(LocalDateTime.now().plusHours(3)).build();
    	final Periodo periodo3 = Periodo.builder().dataHoraInicial(LocalDateTime.now().plusMinutes(30)).dataHoraFinal(LocalDateTime.now().plusHours(1)).build();
    	final Periodo periodo4 = Periodo.builder().dataHoraInicial(LocalDateTime.now().plusMinutes(3)).dataHoraFinal(LocalDateTime.now().plusMinutes(15)).build();
    	final Veiculo veiculo = new Veiculo("ABC-1234", "FUSCA", "VERDE");
    	final Condutor condutor = Condutor
			.builder()
				.cpf("12345678900")
			.build(); 
		final Parquimetro fixo = Parquimetro
			.builder()
				.tipoParquimetro(TipoParquimetro.FIXO)
				.condutor(condutor)
				.veiculo(veiculo)
				.periodos(Arrays.asList(periodo1, periodo2, periodo3, periodo4))
			.build();
    	
		when(veiculoService.findById(anyString())).thenReturn(veiculo.toDTO());
		when(condutorService.findByCpf(anyString())).thenReturn(condutor.toDTO());
    	when(parquimetroRepository.save(fixo)).thenReturn(fixo.toBuilder().id(UUID.randomUUID()).build());
		
    	final ParquimetroDTO parquimetroSalvo = parquimetroService.save(fixo);
		
    	assertNotNull(parquimetroSalvo.getId());
    	verify(periodoUtilService, times(4)).calcularIntervaloHoras(any(LocalDateTime.class), any(LocalDateTime.class));
    	verify(parquimetroRepository).save(fixo);
    }
    
    @Test
    public void deveSalvarParquimetroVariavelQuandoPeriodoInformado() {
    	final Veiculo veiculo = new Veiculo("ABC-1234", "FUSCA", "VERDE");
    	final Condutor condutor = Condutor
			.builder()
				.cpf("12345678900")
			.build(); 
		final Parquimetro variavel = Parquimetro
			.builder()
				.tipoParquimetro(TipoParquimetro.VARIAVEL)
				.condutor(condutor)
				.veiculo(veiculo)
			.build();
    	
		when(veiculoService.findById(anyString())).thenReturn(veiculo.toDTO());
		when(condutorService.findByCpf(anyString())).thenReturn(condutor.toDTO());
    	when(parquimetroRepository.save(variavel)).thenReturn(variavel.toBuilder().id(UUID.randomUUID()).build());
		
    	final ParquimetroDTO parquimetroSalvo = parquimetroService.save(variavel);
		
    	assertNotNull(parquimetroSalvo.getId());
    	verify(parquimetroRepository).save(variavel);
    }
}
