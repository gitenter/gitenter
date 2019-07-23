package com.gitenter.capsid.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.gitenter.capsid.config.bean.DomainSource;

@Configuration
public class DomainConfig {

	@Profile("sts")
	@Bean
	public DomainSource stsdomainSource() {
		
		DomainSource domainSource = new DomainSource();
		domainSource.setDomainName("localhost");
		domainSource.setWebPort(8080);
		domainSource.setGitPort(22);
		return domainSource;
	}
	
	@Profile("docker")
	@Bean
	public DomainSource localhostdomainSource() {
		
		DomainSource domainSource = new DomainSource();
		domainSource.setDomainName("localhost");
		domainSource.setWebPort(8886);
		domainSource.setGitPort(8822);
		return domainSource;
	}
	
	@Profile("staging")
	@Bean
	public DomainSource stagingdomainSource() {
		
		DomainSource domainSource = new DomainSource();
		domainSource.setDomainName("staging.gitenter.com");
		return domainSource;
	}
	
	@Profile("production")
	@Bean
	public DomainSource productiondomainSource() {
		
		DomainSource domainSource = new DomainSource();
		domainSource.setDomainName("gitenter.com");
		return domainSource;
	}
}
