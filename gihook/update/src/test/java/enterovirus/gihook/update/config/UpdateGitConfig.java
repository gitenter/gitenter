package enterovirus.gihook.update.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import enterovirus.gitar.GitSource;

@Configuration
public class UpdateGitConfig {

	@Profile("fake_update")
	@Bean
	public GitSource fakeUpdateGitSource() {
		
		GitSource gitSource = new GitSource();
		gitSource.setRootFolderPath("/home/beta/Workspace/enterovirus-test/hook-fake-update/org/repo.git");
		return gitSource;
	}
	
	@Profile("long_commit_path")
	@Bean
	public GitSource longCommitPathgitSource() {
		
		GitSource gitSource = new GitSource();
		gitSource.setRootFolderPath("/home/beta/Workspace/enterovirus-test/long-commit-path/org/repo.git");
		return gitSource;
	}
}
