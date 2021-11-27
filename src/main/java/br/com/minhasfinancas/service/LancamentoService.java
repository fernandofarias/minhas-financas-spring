package br.com.minhasfinancas.service;

import java.math.BigDecimal;
import java.util.List;

import br.com.minhasfinancas.model.entity.Lancamento;
import br.com.minhasfinancas.model.enums.StatusLancamento;

public interface LancamentoService {

	Lancamento salvar(Lancamento lancamento);
	
	Lancamento atualizar(Lancamento lancamento);
	
	void deletar(Lancamento lancamento);
	
	List<Lancamento> buscar(Lancamento lancamentoFiltro);
	
	void atualizarStatus(Lancamento lancamento, StatusLancamento status);
	
	void validar(Lancamento lancamento);
	
	Lancamento buscarPorId(Long id);
	
	BigDecimal obterSaldoPorUsuario(Long id);
}
