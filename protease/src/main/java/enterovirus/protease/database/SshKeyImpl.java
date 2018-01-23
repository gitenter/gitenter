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
		 * If write to the database is not successful.
		 */
		
		File authorizedKeys = sshSource.getAuthorizedKeysFilepath();
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(authorizedKeys, true))) {
		
			bw.write(sshKey.getKey());
			bw.newLine();
			bw.newLine();
		}
		
		return sshKey;
	}
}
