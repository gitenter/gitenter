package com.gitenter.capsid.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.gitenter.capsid.config.bean.WebDomain;

@Configuration
public class WebDomainConfig {

	@Profile("sts")
	@Bean
	public WebDomain stsWebSource() {
		
		WebDomain webSource = new WebDomain();
		webSource.setDomainName("localhost");
		return webSource;
	}
	
	@Profile("docker")
	@Bean
	public WebDomain localhostWebSource() {
		
		WebDomain webSource = new WebDomain();
		webSource.setDomainName("localhost");
		return webSource;
	}
	
	@Profile("qa")
	@Bean
	public WebDomain qaWebSource() {
		
		WebDomain webSource = new WebDomain();
		webSource.setDomainName("qa.gitenter.com");
		return webSource;
	}
	
	@Profile("production")
	@Bean
	public WebDomain productionWebSource() {
		
		WebDomain webSource = new WebDomain();
		webSource.setDomainName("gitenter.com");
		return webSource;
	}
}
