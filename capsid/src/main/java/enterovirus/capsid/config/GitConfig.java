package enterovirus.capsid.config;

import java.io.File;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import enterovirus.protease.source.GitSource;

@Configuration
public class GitConfig {

	@Profile("sts")
	@Bean
	public GitSource stsGitSource() {
		
		GitSource gitSource = new GitSource();
		gitSource.setRootFolderPath(new File(System.getProperty("user.home"), "Workspace/enterovirus-test/fake_server"));
		return gitSource;
	}
	
	@Profile("localhost")
	@Bean
	public GitSource localhostGitSource() {
		
		GitSource gitSource = new GitSource();
		gitSource.setRootFolderPath("/home/git");
		return gitSource;
	}
	
	@Profile("production")
	@Bean
	public GitSource productionGitSource() {
		
		GitSource gitSource = new GitSource();
		gitSource.setRootFolderPath("/home/git");
		return gitSource;
	}
}
