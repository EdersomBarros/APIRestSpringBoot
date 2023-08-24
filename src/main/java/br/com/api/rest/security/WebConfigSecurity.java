package br.com.api.rest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import br.com.api.rest.service.ImplementacaoUserDatailsService;


/*Mapeia URL, enderecos, autoriza ou bloqueia acesso a URL*/
@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private ImplementacaoUserDatailsService implementacaoUserDatailsService;
	
	/*Configura as solicitações de acesso por http*/
	@Override
		protected void configure(HttpSecurity http) throws Exception {
		
			/*Ativando a proteção contra usuário que não estão validados por token*/
			http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
			
			/*Ativando a permissão para acesso a página inicial do sistema*/
			.disable().authorizeRequests().antMatchers("/").permitAll()
			.antMatchers("/index").permitAll()
			.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
			/*URL de Logout - redireciona após ser deslogar do sistema*/
			.anyRequest().authenticated().and().logout().logoutSuccessUrl("/index")
			/*Mapeia URL de logout*/
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
			
			/*Filtra as requisições de login*/
			.and().addFilterBefore(new JWTLoginFilter("/login", 
					authenticationManager()), 
					UsernamePasswordAuthenticationFilter.class)
			
			/*Filtra demais requisições para verificar a pretenção do TOKEN JWT NO HEADER HTTP*/
			.addFilterBefore(new JWTTokenAutenticacaoFilter(), UsernamePasswordAuthenticationFilter.class);
			
		}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		/*Service que irá consultar usuário no banco*/
		auth.userDetailsService(implementacaoUserDatailsService)
		/*Padrão de codificação de senha*/
		.passwordEncoder(new BCryptPasswordEncoder());
		
	}

}
