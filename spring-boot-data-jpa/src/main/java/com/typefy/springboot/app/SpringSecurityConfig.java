package com.typefy.springboot.app;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

import com.typefy.springboot.app.auth.handler.LoginSuccessHandler;
import com.typefy.springboot.app.models.service.JpaUserDetailsService;

//@EnableGlobalMethodSecurity() me permite aplicar seguridad directamente en los controladores por medio de anotaciones
@EnableGlobalMethodSecurity(securedEnabled=true)
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter
{
	@Autowired
	private LoginSuccessHandler successHandler;
	
	@Autowired
	private DataSource datasource;
	
	@Autowired
	private JpaUserDetailsService userDetailsService;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers( "/", "/css/**", "/js/**","/images/**", "/clientes**", "/locale", "/api/clientes/**"  ).permitAll() //Rutas públicas. De acceso a cualquiera aunque no esté logueado.
		//Rutas privadas asignadas segín el rol.
		
		//Se comentan para revisar otra forma de dar seguridad a los recursos mediante anotaciones en los controladores.
		/*.antMatchers("/detalle/**").hasAnyRole("USER")*/
		/*.antMatchers( "/uploads/**" ).hasAnyRole( "USER" )*/
		/*.antMatchers( "/form/**" ).hasAnyRole( "ADMIN" )*/
		/*.antMatchers( "/eliminar/**" ).hasAnyRole( "ADMIN" )*/
		/*.antMatchers( "/factura/**" ).hasAnyRole( "ADMIN" )*/
		.anyRequest().authenticated()
		//Implementación de formulario de login
		.and().formLogin()
			.successHandler(successHandler)
			.loginPage("/login") //Si no se pone .loginPage("/login"), SpringSecurity va a mostrar la página de logueo que trae por defecto. Aquí se pone es el getMapping del controlador.
			.permitAll()
		//Implementamos página de logout
		.and().logout().permitAll()
		.and().exceptionHandling().accessDeniedPage("/error_403");
	}

	@Autowired
	public void configurerGlobal( AuthenticationManagerBuilder build ) throws Exception
	{
		build.userDetailsService(userDetailsService)
		/*build.jdbcAuthentication()*/
		
		//.dataSource(datasource)
		.passwordEncoder(passwordEncoder);
		
		/*.usersByUsernameQuery("select username, password, enabled from users where username=?")
		.authoritiesByUsernameQuery("select u.username, a.authority from authorities a inner join users u on (a.user_id=u.id) where u.username=?"); */
		
//		UserBuilder users =  User.withDefaultPasswordEncoder();
		
		//Se comenta para empezar a utilizar autenticación por base de datos y no en memoria.
		/*
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		UserBuilder users = User.builder().passwordEncoder(encoder::encode);
		
		build.inMemoryAuthentication()
			.withUser(users.username( "admin" ).password( "123456" ).roles( "ADMIN", "USER" ))
			.withUser( users.username( "jonatahn" ).password( "123456" ).roles( "USER" ) );*/
	}
	
	@Bean 
	public SpringTemplateEngine templateEngine() {
	    SpringTemplateEngine templateEngine = new SpringTemplateEngine();
	    templateEngine.setTemplateResolver(thymeleafTemplateResolver());
	    templateEngine.setEnableSpringELCompiler(true);
	    templateEngine.addDialect(new SpringSecurityDialect());
	    return templateEngine;
	}

	@Bean
	public SpringResourceTemplateResolver thymeleafTemplateResolver() {
	    SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
	    templateResolver.setPrefix("classpath:templates/");
	    templateResolver.setSuffix(".html");
	    templateResolver.setCacheable(false);
	    templateResolver.setTemplateMode(TemplateMode.HTML);
	    return templateResolver;
	}

	@Bean
	public ThymeleafViewResolver thymeleafViewResolver() {
	    ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
	    viewResolver.setTemplateEngine(templateEngine());
	    viewResolver.setCharacterEncoding("UTF-8");
	    return viewResolver;
	}
}
