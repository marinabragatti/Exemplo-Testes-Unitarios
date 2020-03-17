package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Test;

import br.ce.wcaquino.entidades.Usuario;

public class AssertTest {

	@Test
	public void test() {
		Assert.assertTrue(true);
		Assert.assertFalse(false);

		Assert.assertEquals("Erro de comparação", 1, 1);
		Assert.assertEquals(0.512345, 0.512, 0.001);
		Assert.assertEquals(Math.PI, 3.14, 0.01);

		int i = 5;
		Integer i2 = 5;

		Assert.assertEquals(Integer.valueOf(i), i2);
		Assert.assertEquals(i, i2.intValue());

		Assert.assertEquals("bola", "bola");
		Assert.assertTrue("bola".equalsIgnoreCase("Bola"));
		Assert.assertTrue("bola".startsWith("bo"));

		Usuario u1 = new Usuario("usuario 1");
		Usuario u2 = new Usuario("usuario 1");
		Usuario u3 = null;

		Assert.assertEquals(u1, u2); 
		// Essa comparação dá falso, pois os objetos possuem id diferente, são dois objetos distintos
		// Para dar verdadeiro é necessário implementar equals and hash code na classe
		// pois então a comparação é feita no nome e tipo de objeto
		
		Assert.assertSame(u2, u2);
		Assert.assertNotSame(u1, u2);
		
		Assert.assertNull(u3);
		Assert.assertNotNull(u2);
	}
}
