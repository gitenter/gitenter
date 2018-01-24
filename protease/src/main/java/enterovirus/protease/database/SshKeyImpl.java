package enterovirus.protease.database;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.HashSet;
import java.util.Set;

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
		}
		
		/*
		 * chmod 775 authorized_keys
		 * 
		 * TODO: 664?
		 */
		Set<PosixFilePermission> perms = new HashSet<>();
		perms.add(PosixFilePermission.OWNER_READ);
		perms.add(PosixFilePermission.OWNER_WRITE);
		perms.add(PosixFilePermission.OWNER_EXECUTE);
		perms.add(PosixFilePermission.GROUP_READ);
		perms.add(PosixFilePermission.GROUP_WRITE);
		perms.add(PosixFilePermission.GROUP_EXECUTE);
		perms.add(PosixFilePermission.OTHERS_READ);
		perms.add(PosixFilePermission.OTHERS_EXECUTE);
		Files.setPosixFilePermissions(authorizedKeys.toPath(), perms);
		
		/*
		 * chown tomcat:enterovirus authorized_keys
		 */
		UserPrincipalLookupService lookupService = FileSystems.getDefault().getUserPrincipalLookupService();
		PosixFileAttributeView view = Files.getFileAttributeView(authorizedKeys.toPath(), PosixFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
		GroupPrincipal group = lookupService.lookupPrincipalByGroupName("enterovirus");
		view.setGroup(group);
		
		return sshKey;
	}
}
