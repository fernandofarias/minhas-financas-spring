package br.com.minhasfinancas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.minhasfinancas.dto.AtualizaStatusDTO;
import br.com.minhasfinancas.dto.LancamentoDTO;
import br.com.minhasfinancas.exceptions.RegraNegocioException;
import br.com.minhasfinancas.model.entity.Lancamento;
import br.com.minhasfinancas.model.entity.Usuario;
import br.com.minhasfinancas.model.enums.StatusLancamento;
import br.com.minhasfinancas.model.enums.TipoLancamento;
import br.com.minhasfinancas.service.LancamentoService;
import br.com.minhasfinancas.service.UsuarioService;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoController {
	
	@Autowired
	private LancamentoService lancamentoService;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@PostMapping
	public ResponseEntity<?> salvar(@RequestBody LancamentoDTO lancamentoDTO){
		Lancamento lancamento = parser(lancamentoDTO);
		
		try {
			lancamento = lancamentoService.salvar(lancamento);

			return new ResponseEntity<>(lancamento, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
	}
	
	@PutMapping("{id}")
	public ResponseEntity<?> atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO lancamentoDTO){
		Lancamento lancamentoBusca = lancamentoService.buscarPorId(id);

		Lancamento lancamento = parser(lancamentoDTO);
		lancamento.setId(lancamentoBusca.getId());
		
		lancamentoService.atualizar(lancamento);
		
		return ResponseEntity.ok(lancamento);
	}
	
	@PutMapping("{id}/atualiza-status")
	public ResponseEntity<?> atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDTO atualizaStatusDTO){
		StatusLancamento status;
		
		try {
			status = StatusLancamento.valueOf(atualizaStatusDTO.getStatus());
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Informe um Status válido.");
		}
		
		Lancamento lancamentoBusca = lancamentoService.buscarPorId(id);
		
		lancamentoBusca.setStatusLancamento(status);
		
		lancamentoService.atualizar(lancamentoBusca);
		
		return ResponseEntity.ok(lancamentoBusca);
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity<?> deletar(@PathVariable("id") Long id ){
		Lancamento lancamentoBusca = lancamentoService.buscarPorId(id);

		lancamentoService.deletar(lancamentoBusca);
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping
	public ResponseEntity<?> buscar(@RequestParam(value = "descricao", required = false) String descricao,
									@RequestParam(value = "tipoLancamento", required = false) String tipoLancamento,
									@RequestParam(value = "mes", required = false) Integer mes,
									@RequestParam(value = "ano", required = false) Integer ano,
									@RequestParam("idUsuario") Long idUsuario){
		
		List<Lancamento> lancamentos;
		try {
			
			Usuario usuario = usuarioService.buscarPorId(idUsuario);
			
			Lancamento lancamentoFiltro = Lancamento.builder()
													.descricao(descricao)
													.mes(mes)
													.ano(ano)
													.usuario(usuario)
													.tipoLancamento(tipoLancamento != null ? TipoLancamento.valueOf(tipoLancamento) : null)
													.build();
			
			lancamentos = lancamentoService.buscar(lancamentoFiltro);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
		return ResponseEntity.ok(lancamentos);
	}
	
	@GetMapping("{id}")
	public ResponseEntity<?> buscarPorId(@PathVariable("id") Long id ) {
		Lancamento lancamento;
		try {
			lancamento = lancamentoService.buscarPorId(id);
			
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
		return ResponseEntity.ok(lancamento);
	}
	
	private Lancamento parser(LancamentoDTO lancamentoDTO) {
		Usuario usuario = usuarioService.buscarPorId(lancamentoDTO.getUsuario());
		
		return  Lancamento.builder()
                          .id(lancamentoDTO.getId())
                          .descricao(lancamentoDTO.getDescricao())
                          .valor(lancamentoDTO.getValor())
                          .mes(lancamentoDTO.getMes())
                          .ano(lancamentoDTO.getAno())
                          .usuario(usuario)
                          .tipoLancamento(TipoLancamento.valueOf(lancamentoDTO.getTipoLancamento()))
                          .statusLancamento(lancamentoDTO.getStatusLancamento() == null ? StatusLancamento.PENDENTE : StatusLancamento.valueOf(lancamentoDTO.getStatusLancamento()))
                          .build();
	}
	
}
