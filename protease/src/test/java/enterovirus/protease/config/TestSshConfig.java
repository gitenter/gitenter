package enterovirus.protease.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import enterovirus.protease.source.GitSource;
import enterovirus.protease.source.SshSource;

@Configuration
public class TestSshConfig {
	
	@Bean
	public SshSource sshSource() {
		
		SshSource sshSource = new SshSource();
		sshSource.setSshFolderPath("/home/beta/Workspace/enterovirus-test/ssh_tests/.ssh");
		return sshSource;
	}
}
