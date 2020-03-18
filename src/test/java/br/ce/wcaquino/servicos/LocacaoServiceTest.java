package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builder.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builder.FilmeBuilder.umFilmeSemEstoque;
import static br.ce.wcaquino.builder.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprios.caiEm;
import static br.ce.wcaquino.matchers.MatchersProprios.caiNumaSegunda;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHoje;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHojeComDiferencaDias;
import static br.ce.wcaquino.utils.DataUtils.verificarDiaSemana;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeFalse;

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

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.daos.LocacaoDAOFake;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;
import buildermaster.BuilderMaster;

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
		LocacaoDAO dao = new LocacaoDAOFake();
		service.setLocacaoDAO(dao);
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
		assumeFalse(verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		// cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filme = Arrays.asList(umFilme().comValor(5.0).agora());

		// acao
		Locacao locacao = service.alugarFilme(usuario, filme);

		// verificacao ***(é recomendado haver apenas uma verificação por teste)

		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		error.checkThat(locacao.getDataLocacao(), ehHoje());
//		error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
//		error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));

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
			Assert.assertThat(e.getMessage(), is("Usuário vazio"));
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
	public void naoDevolverFilmeNoDomingo() throws FilmeSemEstoqueException, LocadoraException {
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		//acao
		Locacao retorno = service.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(retorno.getDataRetorno(), caiEm(Calendar.MONDAY));
		assertThat(retorno.getDataRetorno(), caiNumaSegunda());
	}
	
	public static void main(String[] args) {
		new BuilderMaster().gerarCodigoClasse(Locacao.class);
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
