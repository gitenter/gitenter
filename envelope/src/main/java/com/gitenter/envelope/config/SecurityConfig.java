package com.gitenter.envelope.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
				.antMatchers("/").authenticated()
//				/*
//				 * URL patterns in "SettingsController".
//				 */
//				.antMatchers("/settings/**").authenticated()
//				/*
//				 * URL patterns in "ManagerAdminController".
//				 */
				.antMatchers("/organizations/create").authenticated()
//				.antMatchers("/organizations/{organizationId}/settings/**").access("@organizationService.isManager(#organization.id, authentication.username)")
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
			.logout().logoutUrl("/logout")
			.and()
			.rememberMe().tokenValiditySeconds(2419200).key("enterovirus");
	}
	
	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
}