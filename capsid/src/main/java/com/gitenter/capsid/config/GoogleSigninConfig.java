package com.gitenter.capsid.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.gitenter.capsid.config.bean.GoogleSignin;

@Configuration
public class GoogleSigninConfig {

	@Profile("local")
	@Bean
	public GoogleSignin sts() {
		GoogleSignin bean = new GoogleSignin();
		bean.setClientId("417755491786-1qpj7mtq8leob3uognt4odi9ihk2rqts");
		return bean;
	}
	
	@Profile("docker")
	@Bean
	public GoogleSignin docker() {
		GoogleSignin bean = new GoogleSignin();
		return bean;
	}
	
	@Profile("staging")
	@Bean
	public GoogleSignin staging() {
		GoogleSignin bean = new GoogleSignin();
		bean.setClientId("166255485673-lrba9e6lc1kivml74ltavr6kmm7luuoi");
		return bean;
	}
	
	@Profile("production")
	@Bean
	public GoogleSignin production() {
		GoogleSignin bean = new GoogleSignin();
		bean.setClientId("618985663779-686fih0r5e051q57qksfuo4ert6t1qu5");
		return bean;
	}
}
