package com.gitenter.hook.postreceive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gitenter.protease.source.SshSource;

@Configuration
public class SshConfig {
	
	@Bean
	public SshSource sshGitSource() {
		
		SshSource sshSource = new SshSource();
		sshSource.setSshFolderPath("/not/relevant/fake/path");
		return sshSource;
	}
}
