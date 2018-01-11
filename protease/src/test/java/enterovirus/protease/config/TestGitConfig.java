package enterovirus.protease.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import enterovirus.gitar.GitSource;

@Configuration
public class TestGitConfig {
	
	@Profile("user_auth")
	@Bean
	public GitSource userAuthGitSource() {
		
		GitSource gitSource = new GitSource();
		
		/* 
		 * Current this folder doesn't exist, but that doesn't matter. 
		 */
		gitSource.setRootFolderPath("dummy-position-not-exist");
		
		return gitSource;
	}

	@Profile("one_repo_fix_commit")
	@Bean
	public GitSource oneRepoFixCommitGitSource() {
		
		GitSource gitSource = new GitSource();
		
		gitSource.setRootFolderPath("/home/beta/Workspace/enterovirus-test/one-repo-fix-commit");
		
		return gitSource;
	}
	
	@Profile("long_commit_path")
	@Bean
	public GitSource longCommitPathgitSource() {
		
		GitSource gitSource = new GitSource();
		gitSource.setRootFolderPath("/home/beta/Workspace/enterovirus-test/long-commit-path/");
		return gitSource;
	}
}
