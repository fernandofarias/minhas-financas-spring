package br.com.minhasfinancas.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.minhasfinancas.exceptions.ErroAutenticacao;
import br.com.minhasfinancas.exceptions.RegraNegocioException;
import br.com.minhasfinancas.model.entity.Usuario;
import br.com.minhasfinancas.model.repository.UsuarioRepository;
import br.com.minhasfinancas.service.impl.UsuarioServiceImpl;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

	@SpyBean
	UsuarioServiceImpl usuarioService;
	
	@MockBean
	UsuarioRepository usuarioRepository;
	
	@Test(expected = Test.None.class)
	public void validarEmail() {
		//cenário
		Mockito.when(usuarioRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
		
		//ação
		usuarioService.validarEmail("fernando@email.com");
		
	}
	
	@Test(expected = RegraNegocioException.class)
	public void validarEmailErro() {
		//cenário
		Mockito.when(usuarioRepository.existsByEmail(Mockito.anyString())).thenReturn(true);
		
		//ação
		usuarioService.validarEmail("fernando@email.com");
	}
	
	@Test(expected = Test.None.class)
	public void autenticarUsuarioSucesso() {
		//cenário
		String email = "fernando@email.com";
		String senha = "123@";
		
		Usuario usuario = Usuario.builder()
								 .email(email)
								 .senha(senha)
								 .id(1L)
								 .build();
		
		Mockito.when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
		
		//ação
		Usuario result = usuarioService.autenticar(email, senha);
		
		//verificação
		Assertions.assertThat(result).isNotNull();
	}
	
	@Test
	public void autenticarUsuarioErroEmail() {
		//cenário
		Mockito.when(usuarioRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

		//ação
		Throwable exception = Assertions.catchThrowable(() -> usuarioService.autenticar("email", "senha"));
		
		//verificação
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Usuario não encontrado.");

	}
	
	@Test
	public void autenticarUsuarioErroSenha() {
		//cenário
		Usuario usuario = Usuario.builder()
								 .nome("fernando")
								 .email("fernando@email.com")
								 .senha("123@")
								 .build();
		
		Mockito.when(usuarioRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
		
		
		//ação
		Throwable exception = Assertions.catchThrowable(() -> usuarioService.autenticar("email", "senha"));
		
		//verificação
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha incorreta.");
	}
	
	@Test(expected = Test.None.class)
	public void salvarUsuarioSucesso() {
		//cenário
		Mockito.doNothing().when(usuarioService).validarEmail(Mockito.anyString());
		
		Usuario usuario = Usuario.builder()
								 .id(1L)
								 .nome("fernando")
								 .email("fernando@email.com")
								 .senha("123@")
								 .build();
		
		Mockito.when(usuarioRepository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		//ação
		Usuario usuarioSalvo = usuarioService.salvarUsuario(new Usuario());
		
		//verificação
		Assertions.assertThat(usuarioSalvo).isNotNull();
		Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1L);
		Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("fernando");
		Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("fernando@email.com");
		Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("123@");
	}
	
	@Test(expected = RegraNegocioException.class)
	public void salvarUsuarioEmailJaCadastrado() {
		//cenário
		String email = "fernando@email.com";
		Usuario usuario = Usuario.builder()
				 				 .email(email)
				 				 .build();
		
		Mockito.doThrow(RegraNegocioException.class).when(usuarioService).validarEmail(email);
		
		//ação
		usuarioService.salvarUsuario(usuario);
		
		//verificação
		Mockito.verify(usuarioRepository, Mockito.never()).save(usuario);
	}
	
}