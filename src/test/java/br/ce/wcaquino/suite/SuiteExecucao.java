package br.ce.wcaquino.suite;

import org.junit.runners.Suite.SuiteClasses;

import br.ce.wcaquino.servicos.CalculadoraTest;
import br.ce.wcaquino.servicos.CalculoValorLocacaoTest;
import br.ce.wcaquino.servicos.LocacaoServiceTest;

//@RunWith(Suite.class) ***Posso fazer todos os testes mandado rodar tudo no pacote testes, sem precisar de suites.
@SuiteClasses({
	CalculadoraTest.class,
	CalculoValorLocacaoTest.class,
	LocacaoServiceTest.class
})
	public class SuiteExecucao{
	//Todos os métodos das classes acima agora rodam de uma vez.

	}