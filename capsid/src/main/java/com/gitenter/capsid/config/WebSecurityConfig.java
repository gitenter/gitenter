package com.gitenter.capsid.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired UserDetailsService userDetailsService;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
			.userDetailsService(userDetailsService)
			.passwordEncoder(encoder());
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
				
		http
			/* 
			 * All the listed packages below needs user authorization.
			 * 
			 * Order: 
			 * Configure the most specific request path patterns 
			 * first and the least specific ones last.
			 * 
			 * Refer to the following link for pattern-related access control:
			 * https://docs.spring.io/spring-security/site/docs/current/reference/html/el-access.html#el-access-web-path-variables
			 */
			.authorizeRequests()
				.antMatchers(HttpMethod.OPTIONS, "/oauth/token").permitAll()
				.antMatchers("/").authenticated()
				.antMatchers("/settings/**").authenticated()
				.antMatchers("/organizations/create").authenticated()
//				.antMatchers("/organizations/{organizationId}/settings/**").access("hasPermission(#organizationId, T(com.gitenter.protease.domain.auth.OrganizationUserRole).MANAGER)")
//				.antMatchers("/organizations/{organizationId}/repositories/create").access("@securityService.checkManagerOfAnOrganization(authentication,#organizationId)")
//				.antMatchers("/organizations/{organizationId}/repositories/{repositoryId}/settings").access("@securityService.checkManagerOfAnOrganization(authentication,#organizationId)")
//				.antMatchers("/organizations/{organizationId}/repositories/{repositoryId}/collaborators/**").access("@securityService.checkManagerOfAnOrganization(authentication,#organizationId)")
//				/*
//				 * URL pattern of "GitNavigationController"
//				 * 
//				 * NOTE:
//				 * For users who's just the manager of an organization (but not 
//				 * any kind of collaborator of the corresponding repository),
//				 * s/he can still access the repository settings/collaborators
//				 * pages (as they are set above).
//				 * 
//				 * TODO:
//				 * Currently this page needs "auth-header". So if user never login,
//				 * can it be redirect to a better header?
//				 */
//				.antMatchers("/organizations/{organizationId}/repositories/{repositoryId}/**").access("@securityService.checkRepositoryReadability(authentication,#repositoryId)")
//				/*
//				 * TODO:
//				 * Need authorization that only people in that organization
//				 * can access the related materials.
//				 */
//				.antMatchers("/organizations/**").authenticated()
//				.anyRequest().permitAll()
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
			/*
			 * TODO:
			 * Currently on AWS ECS /logout will return 403. Try to log into the docker container and
			 * checked various logs, but I can't see and error message. Need to configure why.
			 */
			.logout().logoutUrl("/logout")
			.and()
			.rememberMe().tokenValiditySeconds(2419200).key("enterovirus");
//		
//		http.csrf().disable().authorizeRequests().antMatchers(HttpMethod.OPTIONS, "/oauth/token").permitAll();
	}
	
	/*
	 * TODO:
	 * Should be able to move to a general @Component, rather than setup
	 * in here which is only for web page config.
	 */
	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
	
	/*
	 * TODO:
	 * Should be able to move to a general @Component, rather than setup
	 * in here which is only for web page config.
	 */
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		 DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		 provider.setPasswordEncoder(encoder());
		 provider.setUserDetailsService(userDetailsService);
		 return provider;
	}
	
	/*
	 * https://docs.spring.io/spring-security-oauth2-boot/docs/current/reference/html/boot-features-security-oauth2-authorization-server.html#oauth2-boot-authorization-server-password-grant-autowired-authentication-manager
	 */
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
}