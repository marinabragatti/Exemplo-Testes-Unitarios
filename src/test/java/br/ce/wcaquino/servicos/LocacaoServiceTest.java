package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.matchers.MatchersProprios.caiEm;
import static br.ce.wcaquino.matchers.MatchersProprios.caiNumaSegunda;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHoje;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHojeComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {

	private LocacaoService service;

	// Para colocar várias verificações com mesmo cenário e ação e conseguir ver
	// todas as falhas
	// ao invés de uma por uma, adiciono ErrorCollector.
	@Rule
	public ErrorCollector error = new ErrorCollector();

	@Before
	public void setUp() {
		service = new LocacaoService();
	}

	@After
	public void tearDown() {
		// finalizações após os testes
	}

	@BeforeClass
	public static void setUpClass() {
		// Será executado antes da classe
	}

	@AfterClass
	public static void tearDownClass() {
		// Será executado após o término da classe
	}

	@Test
	public void alugarFilmeComSucesso() throws FilmeSemEstoqueException, LocadoraException {
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		// cenario
		Usuario usuario = new Usuario("usuario 1");
		List<Filme> filme = Arrays.asList(new Filme("Filme 1", 5, 10.5));

		// acao
		Locacao locacao = service.alugarFilme(usuario, filme);

		// verificacao ***(é recomendado haver apenas uma verificação por teste)

		error.checkThat(locacao.getValor(), is(equalTo(10.5)));
		error.checkThat(locacao.getDataLocacao(), ehHoje());
//		error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
//		error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));

	}

	@Test(expected = FilmeSemEstoqueException.class)
	public void alugarFilmeSemEstoque_Excecao() throws Exception {
		// cenario
		Usuario usuario = new Usuario("usuario 1");
		List<Filme> filme = Arrays.asList(new Filme("Filme 1", 0, 10.5));

		// acao
		service.alugarFilme(usuario, filme);
	}

	@Test
	public void alugarFilmeSemUsuario_Excecao() throws FilmeSemEstoqueException {
		// cenario
		List<Filme> filme = Arrays.asList(new Filme("Filme 1", 5, 10.5));

		// acao
		try {
			service.alugarFilme(null, filme);
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), is("Usuário vazio"));
		}
	}

	@Test
	public void alugarFilmeSemFilme_Excecao() throws FilmeSemEstoqueException {
		// cenario
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("usuario 1");

		// acao
		try {
			service.alugarFilme(usuario, null);
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), is("Filme vazio"));
		}
	}
	
	@Test
	public void naoDevolverFilmeNoDomingo() throws FilmeSemEstoqueException, LocadoraException {
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		//cenario
		Usuario usuario = new Usuario();
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 10.0));
		
		//acao
		Locacao retorno = service.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(retorno.getDataRetorno(), caiEm(Calendar.MONDAY));
		assertThat(retorno.getDataRetorno(), caiNumaSegunda());
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
