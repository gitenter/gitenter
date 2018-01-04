package enterovirus.gihook.postreceive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import enterovirus.gitar.GitSource;

@Configuration
public class PostReceiveGitConfig {
	
	@Profile("long_commit_path")
	@Bean
	public GitSource longCommitPathgitSource() {
		
		GitSource gitSource = new GitSource();
		gitSource.setRootFolderPath("/home/beta/Workspace/enterovirus-test/long-commit-path/org/repo.git");
		return gitSource;
	}
	
	@Profile("one_commit_traceability")
	@Bean
	public GitSource oneCommitTraceabilityGitSource() {
		
		GitSource gitSource = new GitSource();
		gitSource.setRootFolderPath("/home/beta/Workspace/enterovirus-test/one-commit-traceability/org/repo.git");
		return gitSource;
	}
	
	@Profile("fake_update")
	@Bean
	public GitSource fakeUpdateGitSource() {
		
		GitSource gitSource = new GitSource();
		gitSource.setRootFolderPath("/home/beta/Workspace/enterovirus-test/hook-fake-update/org/repo.git");
		return gitSource;
	}
}
