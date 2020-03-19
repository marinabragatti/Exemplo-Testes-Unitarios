package br.ce.wcaquino.daos;

import java.util.List;

import br.ce.wcaquino.entidades.Locacao;

public class LocacaoDAOFake implements LocacaoDAO{
	public void salvar(Locacao locacao){
		
	}

	@Override
	public List<Locacao> obterLocacoesPendentes() {
		return null;
	}
}