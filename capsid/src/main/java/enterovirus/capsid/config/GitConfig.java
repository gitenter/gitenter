package enterovirus.capsid.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import enterovirus.protease.source.GitSource;

@Configuration
public class GitConfig {

	@Bean
	public GitSource productionGitSource() {
		
		GitSource gitSource = new GitSource();
		gitSource.setRootFolderPath("/home/git");
		return gitSource;
	}
}
