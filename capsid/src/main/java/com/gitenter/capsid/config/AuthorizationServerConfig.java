package com.gitenter.capsid.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

/*
 * Mostly follows https://www.toptal.com/spring/spring-boot-oauth2-jwt-rest-protection
 * 
 * TODO:
 * `org.springframework.security.oauth2` is in maintainance mode. Use Spring Security 5
 * instead when it is available.
 * https://spring.io/blog/2018/01/30/next-generation-oauth-2-0-support-with-spring-security
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private UserDetailsService userDetailsService;

	@Value("${jwt.clientId:gitenter-envuelope}")
	private String clientId;

	@Value("${jwt.client-secret:secretpassword}")
	private String clientSecret;

	@Value("${jwt.signing-key:123}")
	private String jwtSigningKey;

	@Value("${jwt.accessTokenValidititySeconds:43200}") // 12 hours
	private int accessTokenValiditySeconds;

	@Value("${jwt.authorizedGrantTypes:password,authorization_code,refresh_token}")
	private String[] authorizedGrantTypes;

	@Value("${jwt.refreshTokenValiditySeconds:2592000}") // 30 days
	private int refreshTokenValiditySeconds;

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients
			.inMemory().withClient(clientId).secret(passwordEncoder.encode(clientSecret))
			.accessTokenValiditySeconds(accessTokenValiditySeconds)
			.refreshTokenValiditySeconds(refreshTokenValiditySeconds).authorizedGrantTypes(authorizedGrantTypes)
			.scopes("read", "write").resourceIds("api");
	}

	@Override
	public void configure(final AuthorizationServerEndpointsConfigurer endpoints) {
		endpoints
//			.prefix("/api") // Cannot. Otherwise it `/oauth/token` will get "{"error":"unauthorized","error_description":"Full authentication is required to access this resource"}"
			.accessTokenConverter(accessTokenConverter())
			.userDetailsService(userDetailsService)
			.authenticationManager(authenticationManager);
	}

	@Bean
	JwtAccessTokenConverter accessTokenConverter() {
		 JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		 return converter;
	}
}