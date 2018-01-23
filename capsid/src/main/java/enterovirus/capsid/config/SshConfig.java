package enterovirus.capsid.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import enterovirus.protease.source.SshSource;

@Configuration
public class SshConfig {
	
	@Bean
	public SshSource sshGitSource() {
		
		SshSource sshSource = new SshSource();
		sshSource.setSshFolderPath("/home/git/.ssh");
		return sshSource;
	}
}
