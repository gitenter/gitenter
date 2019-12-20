package com.gitenter.capsid.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
//		.antMatchers(HttpMethod.OPTIONS, "/api/users").permitAll()
		.antMatchers(HttpMethod.POST, "/api/users").permitAll()
		.antMatchers("/api/users/me").authenticated()
//		.antMatchers("/api/glee/**").hasAnyAuthority("ADMIN", "USER")
//		.antMatchers("/api/users/**").hasAuthority("ADMIN")
		.antMatchers("/api/**").authenticated()
//		.antMatchers("/**").permitAll()
		.anyRequest().authenticated()
		.and().exceptionHandling().authenticationEntryPoint(authenticationEntryPoint).accessDeniedHandler(new GitEnterAccessDeniedHandler());
		
		/*
		 * The W3 specification says that preflight requests should never include 
		 * credentials so pass credentials in should not be an option:
		 * https://fetch.spec.whatwg.org/#cors-protocol-and-credentials
		 * 
		 * However, by default Spring will 401 OPTIONS requests just like the other 
		 * ones. Avoid 401 for OPTIONS preflights requests:
		 * https://www.baeldung.com/spring-security-cors-preflight
		 * 
		 * Alternatively, we can `.antMatchers(HttpMethod.OPTIONS, "/api/users").permitAll()`
		 * for each individual endpoint. However, unlike that will give customized 
		 * `Allow: POST,OPTIONS`, here will not return `Allow` header. But that doesn't 
		 * affect axios preflight anyway.
		 */
		http.cors();
	}
	
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				/*
				 * TODO:
				 * Only allow origins from selected front-ends. Use `allowedOrigins`.
				 */
				registry.addMapping("/api/**");
			}
		};
	}
}
