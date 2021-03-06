package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builder.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builder.FilmeBuilder.umFilmeSemEstoque;
import static br.ce.wcaquino.builder.LocacaoBuilder.umLocacao;
import static br.ce.wcaquino.builder.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprios.caiNumaSegunda;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHoje;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHojeComDiferencaDias;
import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterData;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {

	@InjectMocks @Spy
	private LocacaoService service;
	
	@Mock
	private SPCService spc;
	@Mock
	private LocacaoDAO dao;
	@Mock
	private EmailService email;

	// Para colocar v�rias verifica��es com mesmo cen�rio e a��o e conseguir ver
	// todas as falhas
	// ao inv�s de uma por uma, adiciono ErrorCollector.
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown() {
		// finaliza��es ap�s os testes
	}

	@BeforeClass
	public static void setUpClass() {
		// Ser� executado antes da classe
	}

	@AfterClass
	public static void tearDownClass() {
		// Ser� executado ap�s o t�rmino da classe
	}

	@Test
	public void alugarFilmeComSucesso() throws Exception {
		// cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filme = Arrays.asList(umFilme().comValor(5.0).agora());
		
		Mockito.doReturn(DataUtils.obterData(27, 03, 2020)).when(service).obterData();

		// acao
		Locacao locacao = service.alugarFilme(usuario, filme);

		// verificacao ***(� recomendado haver apenas uma verifica��o por teste)

		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		error.checkThat(isMesmaData(locacao.getDataLocacao(), obterData(27, 03, 2020)), is(true));
		error.checkThat(isMesmaData(locacao.getDataRetorno(), obterData(28, 03, 2020)), is(true));

	}

	@Test(expected = FilmeSemEstoqueException.class)
	public void alugarFilmeSemEstoque_Excecao() throws Exception {
		// cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filme = Arrays.asList(umFilmeSemEstoque().agora());

		// acao
		service.alugarFilme(usuario, filme);
	}

	@Test
	public void alugarFilmeSemUsuario_Excecao() throws FilmeSemEstoqueException {
		// cenario
		List<Filme> filme = Arrays.asList(umFilme().agora());

		// acao
		try {
			service.alugarFilme(null, filme);
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), is("Usu�rio vazio"));
		}
	}

	@Test
	public void alugarFilmeSemFilme_Excecao() throws FilmeSemEstoqueException {
		// cenario
		LocacaoService service = new LocacaoService();
		Usuario usuario = umUsuario().agora();

		// acao
		try {
			service.alugarFilme(usuario, null);
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), is("Filme vazio"));
		}
	}
	
	@Test
	public void naoDevolverFilmeNoDomingo() throws Exception {		
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		Mockito.doReturn(DataUtils.obterData(21, 03, 2020)).when(service).obterData();
		
		//acao
		Locacao retorno = service.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(retorno.getDataRetorno(), caiNumaSegunda());
		
	}
	
	@Test
	public void naoAlugarParaNegativadoSPC() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		when(spc.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true);
		
		//acao
		try {
			service.alugarFilme(usuario, filmes);
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), is("Usu�rio Negativado"));
		}
		
		//verificacao
		verify(spc).possuiNegativacao(usuario);
	}
	
	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas() {
		//cenario
		Usuario usuario = umUsuario().agora();
		Usuario usuario2 = umUsuario().comNome("Usu�rio em dia").agora();
		Usuario usuario3 = umUsuario().comNome("Outro atrasado").agora();
		List<Locacao> locacoes = Arrays.asList(
				umLocacao().atrasada().comUsuario(usuario).agora(),
				umLocacao().comUsuario(usuario2).agora(),
				umLocacao().atrasada().comUsuario(usuario3).agora());
		when(dao.obterLocacoesPendentes()).thenReturn(locacoes);
		
		//acoes
		service.notificarAtrasos();
		
		//verificacao
		verify(email, Mockito.times(2)).notificarAtraso(Mockito.any(Usuario.class));
		verify(email).notificarAtraso(usuario);
		verify(email, never()).notificarAtraso(usuario2);
		verify(email, Mockito.atLeastOnce()).notificarAtraso(usuario3);
		verifyNoMoreInteractions(email);
	}
	
	@Test
	public void deveTratarErroSPC() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		when(spc.possuiNegativacao(usuario)).thenThrow(new Exception("Falha!!!"));
		
		//verificacao
		exception.expect(LocadoraException.class);
		exception.expectMessage("Problemas no SPC, tente novamente");
		
		//acao
		service.alugarFilme(usuario, filmes);	
	}
	
	@Test
	public void prorrogarUmaLocacao() {
		//cenario
		Locacao locacao = umLocacao().agora();
		
		//acao
		service.prorrogarLocacao(locacao, 3);
		
		//verificacao
		ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class);
		Mockito.verify(dao).salvar(argCapt.capture());
		Locacao locacaoRetornada = argCapt.getValue();
		
		error.checkThat(locacaoRetornada.getValor(), is(12.0));
		error.checkThat(locacaoRetornada.getDataLocacao(), ehHoje());
		error.checkThat(locacaoRetornada.getDataRetorno(), ehHojeComDiferencaDias(3));
	}
	
	
	@Test
	public void calcularValorLocacao() throws Exception {
		//cenario
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		//acao
		Class<LocacaoService> clazz = LocacaoService.class;
		Method metodo = clazz.getDeclaredMethod("calcularValorLocacao", List.class);
		metodo.setAccessible(true);
		Double valor = (Double) metodo.invoke(service, filmes);
		
		//verificacao
		Assert.assertThat(valor, is(4.0));
	}
}



// Formas para checagem de valores
// Assert.assertEquals(10.4, locacao.getValor(), 0.01);
// Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), new
// Date()));
// Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataRetorno(),
// DataUtils.obterDataComDiferencaDias(1)));

// assertThat(locacao.getValor(), is(equalTo(10.5))); //Ctrl+Shift+M addImport
// para maior fluidez de leitura
// assertThat(locacao.getValor(), is(not(10.6)));
// assertThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
// assertThat(isMesmaData(locacao.getDataRetorno(),
// obterDataComDiferencaDias(1)), is(true));

// verificacao
// Assert.assertTrue(locacao.getValor() == 10.5);
// Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), new
// Date()));
// Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataRetorno(),
// DataUtils.obterDataComDiferencaDias(1)));
