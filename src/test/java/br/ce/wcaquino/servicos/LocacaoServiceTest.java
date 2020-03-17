package br.ce.wcaquino.servicos;


import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
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

public class LocacaoServiceTest {
	
	private LocacaoService service;

	// Para colocar v�rias verifica��es com mesmo cen�rio e a��o e conseguir ver todas as falhas
	// ao inv�s de uma por uma, adiciono ErrorCollector.
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Before
	public void setUp() {
		service = new LocacaoService();
	}
	
	@After
	public void tearDown() {
		//finaliza��es ap�s os testes
	}
	
	@BeforeClass
	public static void setUpClass() {
		//Ser� executado antes da classe
	}
	
	@AfterClass
	public static void tearDownClass() {
		//Ser� executado ap�s o t�rmino da classe
	}

	@Test
	public void teste() throws FilmeSemEstoqueException, LocadoraException {
		//cenario
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("usuario 1");
		Filme filme = new Filme("Filme 1", 5, 10.5);

		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filme);
		

		//verificacao ***(� recomendado haver apenas uma verifica��o por teste)
		
		error.checkThat(locacao.getValor(), is(equalTo(10.5)));
		error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
		
	}
	
	@Test(expected=FilmeSemEstoqueException.class)
	public void testeLocacao_semEstoque() throws Exception {
		//cenario
		Usuario usuario = new Usuario("usuario 1");
		Filme filme = new Filme("Filme 1", 0, 10.5);
		
		//acao
		service.alugarFilme(usuario, filme);
	}	
	
	@Test
	public void testeLocacao_semUsuario() throws FilmeSemEstoqueException {
		//cenario
		Filme filme = new Filme("Filme 1", 2, 10.5);
		
		//acao
		try {
			service.alugarFilme(null, filme);
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), is("Usu�rio vazio"));
		}
	}	
	
	@Test
	public void testeLocacao_semFilme() throws FilmeSemEstoqueException {
		//cenario
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("usuario 1");
		
		//acao
		try {
			service.alugarFilme(usuario, null);
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), is("Filme vazio"));
		} 
	}	
}
		
		// Formas para checagem de valores
		//Assert.assertEquals(10.4, locacao.getValor(), 0.01);
		//Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
		//Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)));
		
		//assertThat(locacao.getValor(), is(equalTo(10.5))); //Ctrl+Shift+M addImport para maior fluidez de leitura
		//assertThat(locacao.getValor(), is(not(10.6)));		
		//assertThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));		
		//assertThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));

		//verificacao
		//Assert.assertTrue(locacao.getValor() == 10.5);
		//Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()));
		//Assert.assertTrue(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)));

