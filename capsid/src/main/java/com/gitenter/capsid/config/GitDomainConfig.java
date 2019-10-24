package com.gitenter.capsid.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.gitenter.capsid.config.bean.GitDomainSource;

@Configuration
public class GitDomainConfig {

	@Profile("local")
	@Bean
	public GitDomainSource stsdomainSource() {
		
		GitDomainSource gitDomainSource = new GitDomainSource();
		gitDomainSource.setDomainName("localhost");
		gitDomainSource.setPort(22);
		return gitDomainSource;
	}
	
	@Profile("docker")
	@Bean
	public GitDomainSource localhostdomainSource() {
		
		GitDomainSource gitDomainSource = new GitDomainSource();
		gitDomainSource.setDomainName("git");
		gitDomainSource.setPort(22);
		return gitDomainSource;
	}
	
	@Profile("staging")
	@Bean
	public GitDomainSource stagingdomainSource() {
		
		GitDomainSource gitDomainSource = new GitDomainSource();
		gitDomainSource.setDomainName("git.staging.gitenter.com");
		gitDomainSource.setPort(22);
		return gitDomainSource;
	}
	
	@Profile("production")
	@Bean
	public GitDomainSource productiondomainSource() {
		
		GitDomainSource gitDomainSource = new GitDomainSource();
		gitDomainSource.setDomainName("git.gitenter.com");
		gitDomainSource.setPort(22);
		return gitDomainSource;
	}
}
