package enterovirus.protease.testcase.user_auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import enterovirus.gitar.GitSource;

@Configuration
public class GitConfig {

	@Bean
	public GitSource gitSource() {
		
		GitSource gitSource = new GitSource();
		
		/* 
		 * Current this folder doesn't exist, but that doesn't matter. 
		 */
		gitSource.setRootFolderPath("/home/beta/Workspace/enterovirus-test/user-auth");
		
		return gitSource;
	}
}
