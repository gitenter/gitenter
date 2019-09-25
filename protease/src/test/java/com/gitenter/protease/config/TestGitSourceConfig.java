package com.gitenter.protease.config;

import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.ResourceUtils;

import com.gitenter.protease.config.bean.GitSource;

@Configuration
public class TestGitSourceConfig {
	
	@Profile("minimal")
	@Bean
	public GitSource minimalGitSource() throws IOException {
		
		GitSource gitSource = mock(GitSource.class);
		
		/*
		 * Minimal includes one single commit on master branch:
		 * 
		 * $ git log
		 * 
		 * commit c36a5aed6e1c9f6a6c59bb21288a9d0bdbe93b73 (HEAD -> master, tag: tag, tag: annotated-tag, origin/master)
		 * Author: Cong-Xin Qiu <ozoox.o@gmail.com>
		 * Date:   Wed Jun 27 07:35:06 2018 -0400
		 *
	     * 	commit
		 * (END)
		 */
		when(gitSource.getBareRepositoryDirectory(matches("organization"), matches("repository")))
			.thenReturn(ResourceUtils.getFile("classpath:repo/minimal.git"));
		
		when(gitSource.getBareRepositoryDirectory(not(eq("organization")), any(String.class)))
			.thenReturn(Files.createTempDirectory("gitSourceFolder").toFile());
		
		when(gitSource.getBareRepositoryDirectory(any(String.class), not(eq("repository"))))
		.thenReturn(Files.createTempDirectory("gitSourceFolder").toFile());
		
		return gitSource;
	}
}
