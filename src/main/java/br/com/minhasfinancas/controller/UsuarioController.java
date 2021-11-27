package br.com.minhasfinancas.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.minhasfinancas.dto.UsuarioDTO;
import br.com.minhasfinancas.exceptions.ErroAutenticacao;
import br.com.minhasfinancas.exceptions.RegraNegocioException;
import br.com.minhasfinancas.model.entity.Usuario;
import br.com.minhasfinancas.service.LancamentoService;
import br.com.minhasfinancas.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
	
	@Autowired
	UsuarioService usuarioService;
	
	@Autowired
	LancamentoService lancamentoService;
	
	@PostMapping
	public ResponseEntity<?> salvar(@RequestBody UsuarioDTO usuarioDTO){
		Usuario usuario = Usuario.builder()
								 .nome(usuarioDTO.getNome())	
								 .email(usuarioDTO.getEmail())
								 .senha(usuarioDTO.getSenha())
								 .build();
		
		try {
			Usuario usuarioSalvo = usuarioService.salvarUsuario(usuario);
			return new ResponseEntity<>(usuarioSalvo, HttpStatus.CREATED);
			
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("/autenticar")
	public ResponseEntity<?> autenticar(@RequestBody UsuarioDTO usuarioDTO){
		try {
			Usuario usuarioAutenticado = usuarioService.autenticar(usuarioDTO.getEmail(), usuarioDTO.getSenha());
			return ResponseEntity.ok(usuarioAutenticado);
			
		} catch (ErroAutenticacao e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
	}
	
	@GetMapping("{id}/saldo")
	public ResponseEntity<?> obterSaldo(@PathVariable("id") Long idUsuario){
		BigDecimal saldo = null;
		
		try {
			usuarioService.buscarPorId(idUsuario);
			
			saldo = lancamentoService.obterSaldoPorUsuario(idUsuario);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
		return ResponseEntity.ok(saldo);
	}
}
