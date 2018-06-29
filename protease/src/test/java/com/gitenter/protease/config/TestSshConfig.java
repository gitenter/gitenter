package com.gitenter.protease.config;

import java.io.File;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gitenter.protease.source.SshSource;

@Configuration
public class TestSshConfig {
	
	@Bean
	public SshSource sshSource() {
		
		SshSource sshSource = new SshSource();
		sshSource.setSshFolderPath(new File(System.getProperty("user.home"), "Workspace/enterovirus-test/ssh_tests/.ssh"));
		return sshSource;
	}
}
