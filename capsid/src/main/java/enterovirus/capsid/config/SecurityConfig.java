package enterovirus.capsid.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired private DataSource dataSource;
	@Autowired private UserDetailsService userDetailsService;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authProvider());
//		auth.jdbcAuthentication().dataSource(dataSource)
//			.usersByUsernameQuery("SELECT username, password, true FROM config.member WHERE username=?")
//			.authoritiesByUsernameQuery("SELECT username, 'ROLE_USER' FROM config.member where username=?");
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
	public DaoAuthenticationProvider authProvider() {
		final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(encoder());
		return authProvider;
	}
	
	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
}