package br.com.api.rest.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.api.rest.model.Usuario;
import br.com.api.rest.repository.UsuarioRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/usuario")
public class IndexController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	
	@GetMapping(value = "v1/{id}", produces = "application/json")
	public ResponseEntity<Usuario> initV1 (@PathVariable(value = "id")Long id) {
		
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		System.out.println("Executando a Vedersão 1");
		return  new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}
	
	@GetMapping(value = "v2/{id}", produces = "application/json")
	public ResponseEntity<Usuario> initV2 (@PathVariable(value = "id")Long id) {
		
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		System.out.println("Executando a Vedersão 2");
		return  new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}
	
	@GetMapping(value = "/{id}/codigovenda/{venda}", produces = "application/json")
	public ResponseEntity<Usuario> relatorio(@PathVariable(value = "id")Long id,
												@PathVariable(value = "venda")Long venda) {
		
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		/*o retorno seria um relatório*/
		return  new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}
	
	/*Métodopara consultar Todos*/
	
	@GetMapping(value = "/", produces = "application/json")
	public ResponseEntity<List<Usuario>> usuario(){
		List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();
		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}
	
	@PostMapping(value = "/",produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario){
		
		for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
			
		}
		String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhaCriptografada);
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
		
		 
	}
	
	@PostMapping(value = "/vendausuario",produces = "application/json")
	public ResponseEntity<Usuario> cadastrarvenda(@RequestBody Usuario usuario){
		
		/*processo de venda*/
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
		
		 
	}
	@PostMapping(value = "/{iduser}/idvenda/{idvenda}", produces = "application/json")
	public ResponseEntity cadastrarvenda(@PathVariable Long iduser, @PathVariable Long idvenda){
		
		/*processo de venda*/
		//Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		return new ResponseEntity("id user: " + iduser + " idvenda: " + idvenda, HttpStatus.OK);
		
		 
	}
	
	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {

		for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);

		}
		
		Usuario usuarioTemporario = usuarioRepository.findUserByLogin(usuario.getLogin());
		
		if (!usuarioTemporario.getSenha().equals(usuario.getSenha())) {/*Senhas diferentes*/

			String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhaCriptografada);
		}
		
		Usuario usuarioSalvo = usuarioRepository.save(usuario);

		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);

	}
	
	@SuppressWarnings("rawtypes")
	@DeleteMapping(value = "/{id}",produces = "application/text")
	public String delete(@PathVariable("id") Long id) {
		
		usuarioRepository.deleteById(id);
		
		return "Ok! Apagado";
		
	}
	
	

}
