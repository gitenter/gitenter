package enterovirus.protease.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import enterovirus.protease.source.GitSource;
import enterovirus.protease.source.SshSource;

@Configuration
public class TestSshConfig {
	
	@Bean
	public SshSource sshGitSource() {
		
		SshSource sshSource = new SshSource();
		sshSource.setSshFolderPath("/home/git/.ssh");
		return sshSource;
	}
}