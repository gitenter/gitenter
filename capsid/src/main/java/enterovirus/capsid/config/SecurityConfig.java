package enterovirus.capsid.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired private DataSource dataSource;
	
	/*
	 * TODO:
	 * It is kind of ugly/non-uniform here that the login query 
	 * is not gone from the persistent layer.
	 * Find a better way to do it.
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication().dataSource(dataSource)
			.usersByUsernameQuery("SELECT username, password, true FROM config.member WHERE username=?")
			.authoritiesByUsernameQuery("SELECT username, 'ROLE_USER' FROM config.member where username=?")
			.passwordEncoder(encoder());
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			/* All the listed packages below needs user authorization. */
			.authorizeRequests()
				.antMatchers("/").authenticated()
				.antMatchers("/preference/**").authenticated()
				.antMatchers("/settings/**").authenticated()
				/*
				 * TODO:
				 * Need authorization that only people in that organization
				 * can access the related materials.
				 */
				.antMatchers("/organizations/**").authenticated()
				.anyRequest().permitAll()
			/* 
			 * TODO: 
			 * Do we need pages to require HTTPS for encrypt data transfer? 
			 * BTW, currently the setup below doesn't work.
			 */
//			.and()
//			.requiresChannel()
//				.antMatchers("/preference/tags/").requiresSecure()
			.and()
			.formLogin().loginPage("/login")
			.and()
			.logout().logoutUrl("/logout")
			.and()
			.rememberMe().tokenValiditySeconds(2419200).key("enterovirus");
	}
	
	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
}