package com.gitenter.capsid.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.gitenter.protease.source.WebSource;

@Configuration
public class WebConfig {

	@Profile("sts")
	@Bean
	public WebSource stsWebSource() {
		
		WebSource webSource = new WebSource();
		webSource.setDomainName("localhost");
		return webSource;
	}
	
	@Profile("localhost")
	@Bean
	public WebSource localhostWebSource() {
		
		WebSource webSource = new WebSource();
		webSource.setDomainName("localhost");
		return webSource;
	}
	
	@Profile("production")
	@Bean
	public WebSource productionWebSource() {
		
		WebSource webSource = new WebSource();
		webSource.setDomainName("gitenter.com");
		return webSource;
	}
}
