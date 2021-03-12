package com.spring.boot.rocks.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = "com.spring.boot.rocks")
public class ConfigWebSecurity extends WebSecurityConfigurerAdapter {
	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	DataSource dataSource;

	@Autowired
	@Qualifier("persistentTokenRepository")
	private PersistentTokenRepository persistentTokenRepository;

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.authorizeRequests()
				.antMatchers("/login", "/resources/**", "/webjars/**", "/css/**", "/js/**", "/", "/home", "/img/**",
						"/appUserProfile/img/**",  "/deleteDocument/img/**", "/addDocument/img/**",
						"/appUserProfile/**/img/**", "/userProfilesList/img/**", "/userEdit/img/**",
						"/dbFilesList/img/**", "/userRegister", "/verify/**", "/dbFilesListByName/img/**", "/myDocuments/img/**","/addDocumentModal/img**", "/alfresco/**", "/deleteTheAlfNode/img/**", "/downloadAlfFile/**","/dbfileslist2/img/**")
				.permitAll();
		httpSecurity.authorizeRequests().antMatchers("/delete-user-**").access("hasAuthority('ADMIN')")
				.antMatchers("/userEdit/**").access("hasAuthority('ADMIN')")
//				.antMatchers("/userEdit/**").access("hasAuthority('ADMIN') or hasAuthority('EDITOR')")
				.antMatchers("/deletetheuser/**").access("hasAuthority('ADMIN')")
				.antMatchers("/deleteUserProfile/**").access("hasAuthority('ADMIN')")
				.antMatchers("/deleteFile/**").access("hasAuthority('ADMIN')").antMatchers("/deleteDocument/**")
				.access("hasAuthority('ADMIN')").antMatchers(HttpMethod.POST, "/api/get-data")
				.access("hasAuthority('ADMIN')").antMatchers(HttpMethod.PUT, "/api/findappuser/**")
				.access("hasAuthority('ADMIN')").antMatchers(HttpMethod.PATCH, "/api/listappusers/**")
				.access("hasAuthority('ADMIN')").antMatchers(HttpMethod.DELETE, "/api/deleteuser/**")
				.access("hasAuthority('ADMIN')")

				.anyRequest().authenticated().and().formLogin().loginPage("/login").loginProcessingUrl("/login")
//				.defaultSuccessUrl("/dbfileslist", true)
				.permitAll().and().logout().logoutSuccessUrl("/home").permitAll().and().rememberMe()
				.rememberMeParameter("remember-me").tokenRepository(persistentTokenRepository)
				.userDetailsService(userDetailsService).and().csrf().disable().exceptionHandling()
				.accessDeniedPage("/error");
	}

	@Bean
	public AuthenticationManager customAuthenticationManager() throws Exception {
		return authenticationManager();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/resources/**");
	}

	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
		tokenRepository.setDataSource(dataSource);
		return tokenRepository;
	}

}