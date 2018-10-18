package com.gitenter.hook.postreceive.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.ResourceUtils;

import com.gitenter.protease.source.GitSource;

@Configuration
public class TestGitConfig {
	
	@Profile("minimal")
	@Bean
	public GitSource minimalGitSource() throws FileNotFoundException {
		
		GitSource gitSource = mock(GitSource.class);
		when(gitSource.getBareRepositoryDirectory(any(String.class), any(String.class)))
			.thenReturn(ResourceUtils.getFile("classpath:repo/minimal.git"));
		
		return gitSource;
	}
}
