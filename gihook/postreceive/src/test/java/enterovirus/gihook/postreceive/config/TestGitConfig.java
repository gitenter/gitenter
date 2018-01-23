package enterovirus.gihook.postreceive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import enterovirus.protease.source.GitSource;

@Configuration
public class TestGitConfig {
	
	@Profile("long_commit_path")
	@Bean
	public GitSource longCommitPathgitSource() {
		
		GitSource gitSource = new GitSource();
		gitSource.setRootFolderPath("/home/beta/Workspace/enterovirus-test/long_commit_path/");
		return gitSource;
	}
	
	@Profile("one_commit_traceability")
	@Bean
	public GitSource oneCommitTraceabilityGitSource() {
		
		GitSource gitSource = new GitSource();
		gitSource.setRootFolderPath("/home/beta/Workspace/enterovirus-test/one_commit_traceability/");
		return gitSource;
	}
}
