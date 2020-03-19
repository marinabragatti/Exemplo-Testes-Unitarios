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
		//MOCK : qdo não encontra os parâmetros esperados, retorna 0.
		Mockito.when(calcMock.somar(1, 2)).thenReturn(5);
		Mockito.when(calcMock.somar(1, 2)).thenCallRealMethod();//Para executar o método com Mock.
		
		//SPY : qdo não encontra os parâmetros esperados, executa o método.
		// Não funciona com interfaces.
		Mockito.when(calcSpy.somar(1, 2)).thenReturn(5); //Aqui ele executa o que tem no método, sem o return.
		Mockito.doNothing().when(calcSpy).imprime();//Para o Spy não retornar a execução do método void.
		Mockito.doReturn(5).when(calcSpy).somar(1, 2);//Escrever desta forma para que o retorno seja o esperado,
													  //e não a execução do método. 
		
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
