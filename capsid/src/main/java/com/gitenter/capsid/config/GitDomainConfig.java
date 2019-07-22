package com.gitenter.capsid.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.gitenter.capsid.config.bean.GitDomain;

@Configuration
public class GitDomainConfig {

	@Profile("sts")
	@Bean
	public GitDomain stsgitDomain() {
		
		GitDomain gitDomain = new GitDomain();
		gitDomain.setDomainName("localhost");
		gitDomain.setPort(22);
		return gitDomain;
	}
	
	@Profile("docker")
	@Bean
	public GitDomain localhostgitDomain() {
		
		GitDomain gitDomain = new GitDomain();
		gitDomain.setDomainName("localhost");
		gitDomain.setPort(8822);
		return gitDomain;
	}
	
	@Profile("qa")
	@Bean
	public GitDomain qagitDomain() {
		
		GitDomain gitDomain = new GitDomain();
		gitDomain.setDomainName("qa.gitenter.com");
		gitDomain.setPort(22);
		return gitDomain;
	}
	
	@Profile("production")
	@Bean
	public GitDomain productiongitDomain() {
		
		GitDomain gitDomain = new GitDomain();
		gitDomain.setDomainName("gitenter.com");
		gitDomain.setPort(22);
		return gitDomain;
	}
}
