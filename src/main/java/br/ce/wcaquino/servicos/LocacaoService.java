package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoService {

	private LocacaoDAO dao;
	private SPCService spcService;
	private EmailService emailService;
	
	public Locacao alugarFilme(Usuario usuario, List<Filme> filme) throws FilmeSemEstoqueException, LocadoraException {
				
		if(usuario == null) {
			throw new LocadoraException("Usuário vazio");
		}
		
		if(filme == null || filme.isEmpty()) {
			throw new LocadoraException("Filme vazio");
		}
		
		for(Filme filmes : filme) {
			if(filmes.getEstoque() == 0) {
				throw new FilmeSemEstoqueException();
			}
		}
		
		boolean negativado;
		
		try {
			negativado = spcService.possuiNegativacao(usuario);
		} catch (Exception e) {
			throw new LocadoraException("Problemas no SPC, tente novamente");
		}
		if(negativado) {
			throw new LocadoraException("Usuário Negativado");
		}
		
	
		Locacao locacao = new Locacao();
		locacao.setFilme(filme);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());
		locacao.setValor(calcularValorLocacao(filme));

		//Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		if(DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY)) {
			dataEntrega = adicionarDias(dataEntrega, 1);
		}
		locacao.setDataRetorno(dataEntrega);
		
		//Salvando a locacao...	
		dao.salvar(locacao);
		
		return locacao;
	}

	private double calcularValorLocacao(List<Filme> filme) {
		double valorTotal = 0d;
		for(int i = 0; i < filme.size(); i++) {
			Filme filmes = filme.get(i);
			Double valorFilme = filmes.getPrecoLocacao();
			if(i == 2) {
				valorFilme = valorFilme * 0.75;
			}
			if(i == 3) {
				valorFilme = valorFilme * 0.5;
			}
			if(i == 4) {
				valorFilme = valorFilme * 0.25;
			}
			if(i == 5) {
				valorFilme = 0d;
			}
			valorTotal += valorFilme;
		}
		return valorTotal;
	}
	
	public void notificarAtrasos() {
		List<Locacao> locacoes = dao.obterLocacoesPendentes();
		for(Locacao locacao: locacoes) {
			if(locacao.getDataRetorno().before(new Date())) {
				emailService.notificarAtraso(locacao.getUsuario());
			}
		}
	}
	
	public void prorrogarLocacao(Locacao locacao, int dias) {
		Locacao novaLocacao = new Locacao();
		novaLocacao.setUsuario(locacao.getUsuario());
		novaLocacao.setFilme(locacao.getFilme());
		novaLocacao.setDataLocacao(new Date());
		novaLocacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(dias));
		novaLocacao.setValor(locacao.getValor() * dias);
		dao.salvar(novaLocacao);
	}
}