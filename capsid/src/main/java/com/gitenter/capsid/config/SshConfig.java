package com.gitenter.capsid.config;

import java.io.File;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.gitenter.protease.source.SshSource;

@Configuration
public class SshConfig {

	@Profile("sts")
	@Bean
	public SshSource stsSshGitSource() {
		
		SshSource sshSource = new SshSource();
		sshSource.setSshFolderPath(new File(System.getProperty("user.home"), "Workspace/gitenter-test/local-git-server/.ssh"));
		return sshSource;
	}
	
	@Profile("docker")
	@Bean
	public SshSource localhostSshGitSource() {
		
		SshSource sshSource = new SshSource();
		sshSource.setSshFolderPath("/home/git/.ssh");
		return sshSource;
	}
	
	@Profile("qa")
	@Bean
	public SshSource qaSshGitSource() {
		
		SshSource sshSource = new SshSource();
		sshSource.setSshFolderPath("/home/git/.ssh");
		return sshSource;
	}
	
	@Profile("production")
	@Bean
	public SshSource productionSshGitSource() {
		
		SshSource sshSource = new SshSource();
		sshSource.setSshFolderPath("/home/git/.ssh");
		return sshSource;
	}
}
