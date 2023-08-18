package br.com.api.rest.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import br.com.api.rest.ApplicationContextLoad;
import br.com.api.rest.model.Usuario;
import br.com.api.rest.repository.UsuarioRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Component
public class JWTTokenAutenticacaoService {
	
	/*Tempo de validade do token 2 dias*/
	private static final long EXPIRATION_TIME = 172800000;
	/*Uma senha única para compor a autenticação*/
	private static final String SECRET = "SenhaSecreta";
	/*Prefixo padrão de Token*/
	private static final String TOKEN_PREFIX = "Bearer";
	
	private static final String HEADER_STRING = "Authorization";
	
	/*Gerando token de autenticação e adicionando ao cabeçalho a resposta http*/
	public void addAuthentication(HttpServletResponse response, String username) throws IOException {
		
		/*Montagem do token*/
		String JWT = Jwts.builder()/*Chama o gerador de token*/
				.setSubject(username)/*adiciona o usuario*/
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, SECRET).compact();
		
		/*junta o token com o prefixo*/
		String  token = TOKEN_PREFIX + " " + JWT;
		/*adiciona no cabeçalho http*/
		response.addHeader(HEADER_STRING, token);
		/*Escreve token como resposta no corpo http*/
		response.getWriter().write("{\"Authorization\": \""+token+ "\"}");
		
	}
	
	/*Retorna o usuário validado com token ou casonão seja válido retorna null*/
	public Authentication getAuthentication(HttpServletRequest request) {
		
		/*Pega o token enviado no cabeçalho http*/
		String token = request.getHeader(HEADER_STRING);
		
		if (token != null) {
			/* faz a validação do token do usuário na requisição */
			String user = Jwts.parser().setSigningKey(SECRET)
					.parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
					.getBody()
					.getSubject();

			if (user != null) {

				Usuario usuario = ApplicationContextLoad.getApplicationContext()
						.getBean(UsuarioRepository.class)
						.findUserByLogin(user);

				/* Retornar o usuário logado */
				if (usuario != null) {
					return new UsernamePasswordAuthenticationToken(usuario.getLogin(), 
							usuario.getSenha(),
							usuario.getAuthorities());
				}

			}

		}
		return null;/* não autorizado */
	}

	

}
