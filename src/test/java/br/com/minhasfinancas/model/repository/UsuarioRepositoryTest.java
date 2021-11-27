package br.com.minhasfinancas.model.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.minhasfinancas.model.entity.Usuario;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UsuarioRepositoryTest {

	@Autowired
	UsuarioRepository usuarioRepository;
	
	@Autowired
	TestEntityManager testEntityManager;
	
	@Test
	public void validarSeEmailExiste() {
		//cenário
		Usuario usuario = criarUsuario();
		
		testEntityManager.persist(usuario);
		
		//ação
		boolean emailJaExistente = usuarioRepository.existsByEmail("fernando@gmail.com");
		
		//verificação
		Assertions.assertThat(emailJaExistente).isTrue();
	}
	
	@Test
	public void emailNaoExiste() {
		//cenário
		
		//ação
		boolean emailNaoExistente = usuarioRepository.existsByEmail("fernando@gmail.com");
		
		//verificação
		Assertions.assertThat(emailNaoExistente).isFalse();
	}
	
	@Test
	public void SalvarUsuario() {
		//cenário
		Usuario usuario = criarUsuario();
		//ação
		Usuario user = usuarioRepository.save(usuario);
		
		//verificação
		Assertions.assertThat(user.getId()).isNotNull();
	}
	
	@Test
	public void buscarUsuarioPorEmail() {
		//cenário
		Usuario usuario = criarUsuario();
		testEntityManager.persist(usuario);
		
		//ação
		Optional<Usuario> user = usuarioRepository.findByEmail("fernando@gmail.com");
		
		//verificação
		Assertions.assertThat(user.isPresent()).isTrue();
		
	}
	
	@Test
	public void buscarUsuarioQueNaoExistePorEmail() {
		//cenário
		
		//ação
		Optional<Usuario> user = usuarioRepository.findByEmail("fernando@gmail.com");
		
		//verificação
		Assertions.assertThat(user.isPresent()).isFalse();
		
	}
	
	public static Usuario criarUsuario() {
		return Usuario.builder()
				 	  .nome("Fernando")
				 	  .email("fernando@gmail.com")
				 	  .senha("123@")
				 	  .build();
	}
}
