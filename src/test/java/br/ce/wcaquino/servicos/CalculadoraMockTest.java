package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class CalculadoraMockTest {

	@Mock
	private Calculadora calcMock;
	
	@Spy
	private Calculadora calcSpy;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void diferencaMockESpy() {
		//MOCK : qdo n�o encontra os par�metros esperados, retorna 0.
		Mockito.when(calcMock.somar(1, 2)).thenReturn(5);
		Mockito.when(calcMock.somar(1, 2)).thenCallRealMethod();//Para executar o m�todo com Mock.
		
		//SPY : qdo n�o encontra os par�metros esperados, executa o m�todo.
		// N�o funciona com interfaces.
		Mockito.when(calcSpy.somar(1, 2)).thenReturn(5); //Aqui ele executa o que tem no m�todo, sem o return.
		Mockito.doNothing().when(calcSpy).imprime();//Para o Spy n�o retornar a execu��o do m�todo void.
		Mockito.doReturn(5).when(calcSpy).somar(1, 2);//Escrever desta forma para que o retorno seja o esperado,
													  //e n�o a execu��o do m�todo. 
		
		System.out.println("Mock: " + calcMock.somar(1, 2));
		System.out.println("Spy: " + calcSpy.somar(1, 2));
		System.out.println("Spy");
		calcSpy.imprime();
	}
	
	
	
	@Test
	public void teste() {
		Calculadora calc = Mockito.mock(Calculadora.class);
		
		ArgumentCaptor<Integer> argCapt = ArgumentCaptor.forClass(Integer.class);
		Mockito.when(calc.somar(argCapt.capture(), argCapt.capture())).thenReturn(5);
		
		Assert.assertEquals(5, calc.somar(50, -20));
		//System.out.println(argCapt.getAllValues());
	}
}
