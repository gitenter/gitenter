package com.gitenter.capsid.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.gitenter.capsid.security.GitEnterAccessDeniedHandler;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
	
	@Autowired AuthenticationEntryPoint authenticationEntryPoint;
	
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) {
		resources.resourceId("api");
	}
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
	
		/*
		 * Right now, if not authenticated, Spring will return 200 with empty body.
		 * Compare it with other choices (404 or 204), it is suggested by
		 * https://medium.com/@santhoshkumarkrishna/http-get-rest-api-no-content-404-vs-204-vs-200-6dd869e3af1d
		 */
		http
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
		.antMatcher("/api/**").authorizeRequests()
		.antMatchers("/health_check").permitAll()
		.antMatchers(HttpMethod.POST, "/api/users").permitAll()
		.antMatchers("/api/users/me").authenticated()
//		.antMatchers("/api/glee/**").hasAnyAuthority("ADMIN", "USER")
//		.antMatchers("/api/users/**").hasAuthority("ADMIN")
		.antMatchers("/api/**").authenticated()
//		.antMatchers("/**").permitAll()
		.anyRequest().authenticated()
		.and().exceptionHandling().authenticationEntryPoint(authenticationEntryPoint).accessDeniedHandler(new GitEnterAccessDeniedHandler());		
	}
}
