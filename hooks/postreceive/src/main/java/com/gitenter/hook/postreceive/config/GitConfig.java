package com.gitenter.hook.postreceive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.gitenter.protease.source.GitSource;

@Configuration
public class GitConfig {

	@Profile("production")
	@Bean
	public GitSource gitSource() {
		
		GitSource gitSource = new GitSource();
		gitSource.setRootFolderPath("/not/relevant/fake/path");
		return gitSource;
	}
}
