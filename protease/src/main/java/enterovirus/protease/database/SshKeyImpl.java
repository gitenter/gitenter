package enterovirus.protease.database;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import enterovirus.protease.domain.*;
import enterovirus.protease.source.SshSource;

@Repository
class SshKeyImpl implements SshKeyRepository {

	@Autowired private SshKeyDatabaseRepository sshKeyDbRepository;
	@Autowired private SshSource sshSource;
	
	public SshKeyBean saveAndFlush(SshKeyBean sshKey) throws IOException {

		sshKeyDbRepository.saveAndFlush(sshKey);
		/*
		 * TODO:
		 * Handle the case if write to the database is not successful.
		 */
		
		/*
		 * TODO:
		 * Consider using "ssh-copy-id"
		 * https://www.ssh.com/ssh/copy-id
		 * Relatively safer compare to manually change the 
		 * ".ssh/authorized_keys" file.
		 * 
		 * TODO:
		 * Consider using a Java SSH API, such as 
		 * 1. Java Secure Channel (SJch): http://www.jcraft.com/jsch/
		 * 2. sshj: https://github.com/hierynomus/sshj
		 * 3. Apache sshd: http://mina.apache.org/sshd-project/
		 * rather than manually handle the SSH setups.
		 * (Compare: sshj has a much more concise API than JSch.)
		 * 
		 * TODO:
		 * Or do it through gitolite.
		 */
		File authorizedKeys = sshSource.getAuthorizedKeysFilepath();
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(authorizedKeys, true))) {
		
			bw.write(sshKey.getKey());
			bw.newLine();
		}
		
		return sshKey;
	}
}
