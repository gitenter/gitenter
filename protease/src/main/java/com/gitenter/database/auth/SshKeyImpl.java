package com.gitenter.database.auth;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gitenter.domain.auth.SshKeyBean;
import com.gitenter.protease.source.SshSource;

@Repository
class SshKeyImpl implements SshKeyRepository {

	@Autowired private SshKeyDatabaseRepository sshKeyDbRepository;
	@Autowired private SshSource sshSource;
	
	public SshKeyBean saveAndFlush(SshKeyBean sshKey, String username) throws IOException {

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
		 * Since keys may also be removed, this one should probably
		 * load the database and rewrite the entire "authorized_keys"
		 * file.
		 */
		File authorizedKeys = sshSource.getAuthorizedKeysFilepath();
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(authorizedKeys, true))) {
			
			bw.write("command=\"./git-authorization.sh "+username+"\",no-port-forwarding,no-x11-forwarding,no-agent-forwarding,no-pty");
			bw.write(" ");
			bw.write(sshKey.toString());
			bw.newLine();
		}
		
		return sshKey;
	}
}
