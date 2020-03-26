package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.ce.wcaquino.exceptions.NaoPodeDividirPorZeroException;
import br.ce.wcaquino.runners.ParallelRunner;

@RunWith(ParallelRunner.class)
public class CalculadoraTest {

	private Calculadora calc;
	
	@Before
	public void setUp() {
		calc = new Calculadora();
	}
	
	@Test
	public void deveSomarDoisvalores() {
		//cenario
		int a = 5;
		int b = 3;
		
		// acao
		int resultado = calc.somar(a, b);
		
		// verificacao
		Assert.assertEquals(8, resultado);
	}
	
	@Test
	public void deveSubtrairDoisValores() {
		//cenario
		int a = 5;
		int b = 3;
		
		//acao
		int resultado = calc.subtrair(a, b);
		
		//verificacao
		Assert.assertEquals(2, resultado);
	}
	
	@Test
	public void deveDividirDoisValores() throws NaoPodeDividirPorZeroException {
		//cenario
		int a = 10;
		int b = 5;
		
		//acao
		int resultado = calc.divide(a, b);
		
		//verificacao
		Assert.assertEquals(2, resultado);
	}
	
	@Test(expected = NaoPodeDividirPorZeroException.class)
	public void deveLancarExcecaoAoDividirPorZero() throws NaoPodeDividirPorZeroException {
		//cenario
		int a = 10;
		int b = 0;
		
		//acao
		calc.divide(a, b);
	}
}
