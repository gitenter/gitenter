package enterovirus.capsid.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import enterovirus.protease.source.SshSource;

@Configuration
public class SshConfig {

	@Profile("sts")
	@Bean
	public SshSource stsSshGitSource() {
		
		SshSource sshSource = new SshSource();
		sshSource.setSshFolderPath("/home/beta/.ssh");
		return sshSource;
	}
	
	@Profile("localhost")
	@Bean
	public SshSource localhostSshGitSource() {
		
		SshSource sshSource = new SshSource();
		sshSource.setSshFolderPath("/home/git/.ssh");
		return sshSource;
	}
	
	@Profile("production")
	@Bean
	public SshSource productionSshGitSource() {
		
		SshSource sshSource = new SshSource();
		sshSource.setSshFolderPath("/home/git/.ssh");
		return sshSource;
	}
}
