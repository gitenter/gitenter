package com.gitenter.capsid.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.gitenter.capsid.config.source.DomainSourceBean;

@Configuration
public class DomainConfig {

	@Profile("sts")
	@Bean
	public DomainSourceBean stsdomainSource() {
		
		DomainSourceBean domainSource = new DomainSourceBean();
		domainSource.setDomainName("localhost");
		domainSource.setWebPort(8080);
		domainSource.setGitPort(22);
		return domainSource;
	}
	
	@Profile("docker")
	@Bean
	public DomainSourceBean localhostdomainSource() {
		
		DomainSourceBean domainSource = new DomainSourceBean();
		domainSource.setDomainName("localhost");
		domainSource.setWebPort(8886);
		domainSource.setGitPort(8822);
		return domainSource;
	}
	
	@Profile("staging")
	@Bean
	public DomainSourceBean stagingdomainSource() {
		
		DomainSourceBean domainSource = new DomainSourceBean();
		domainSource.setDomainName("staging.gitenter.com");
		return domainSource;
	}
	
	@Profile("production")
	@Bean
	public DomainSourceBean productiondomainSource() {
		
		DomainSourceBean domainSource = new DomainSourceBean();
		domainSource.setDomainName("gitenter.com");
		return domainSource;
	}
}
