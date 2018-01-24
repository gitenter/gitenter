package enterovirus.protease.source;

import java.io.File;
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

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GitSource {

	/*
	 * This class is similar to javax.sql.DataSource
	 * (or org.springframework.jdbc.datasource.DriverManagerDataSource)
	 * in JDBC. Will think carefully how to implement it
	 * later.
	 */
	private String rootFolderPath;

	public File getOrganizationDirectory (String ownerName) {
		return new File(rootFolderPath, ownerName);
	}
	
	public boolean mkdirOrganizationDirectory (String ownerName) throws IOException {
		
		File directory = new File(rootFolderPath, ownerName);
		boolean result = directory.mkdir();
		
		chmodAndChown(directory);
		
		return result;
	}
	
	/*
	 * Bare git repository at "/rootFolderPath/ownerName/repositoryName.git" .
	 */
	public File getBareRepositoryDirectory (String ownerName, String repositoryName) {
		return new File(new File(rootFolderPath, ownerName), repositoryName+".git");
	}
	
	public boolean mkdirBareRepositoryDirectory (String ownerName, String repositoryName) throws IOException {
		
		File directory = new File(new File(rootFolderPath, ownerName), repositoryName+".git");
		boolean result = directory.mkdir();
		
		chmodAndChown(directory);
		
		return result;
	}
	
	public static String getBareRepositoryOrganizationName (File bareRepositoryDirectory) {
		String[] parts = bareRepositoryDirectory.getPath().split("/");
		return parts[parts.length-2];
	}
	
	public static String getBareRepositoryName (File bareRepositoryDirectory) {
		String[] parts = bareRepositoryDirectory.getPath().split("/");
		String gitFolder = parts[parts.length-1];
		return gitFolder.substring(0, gitFolder.length()-4);
	}
	
	private void chmodAndChown(File directory) throws IOException {
		
		/*
		 * TODO:
		 * 
		 * This is quite dirty. The main aim is to change the
		 * file ownership, so both user "tomcat8" and user "git"
		 * (they are in the same group called "enterovirus)
		 * have full authorization to work on the git file system.
		 * 
		 * The other possibility is to let tomcat to run under the
		 * user "git". However, I can't make it succeed. 
		 * 
		 * See the comments on the "server-side.sh" setups.
		 */
		
		/*
		 * chmod 775 [path-of-the-directory]
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
		Files.setPosixFilePermissions(directory.toPath(), perms);
		
		/*
		 * chown git:enterovirus [path-of-the-directory]
		 */
		UserPrincipalLookupService lookupService = FileSystems.getDefault().getUserPrincipalLookupService();
		PosixFileAttributeView view = Files.getFileAttributeView(directory.toPath(), PosixFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
		GroupPrincipal group = lookupService.lookupPrincipalByGroupName("enterovirus");
		view.setGroup(group);
		/*
		 * TODO:
		 * Cannot change owner in here, because I need "sudo" but there's
		 * no easy way to do it in Java.
		 * 
		 * Right now it is just okay, because everybody is in the "enterovirus"
		 * group, and everybody in that group get the "rwx" permission.
		 * But probably we should do it in a better way.
		 * 
		 * > $ chown original-owner:enterovirus /path/to/the/repo.git
		 * > ok
		 * > $ chown git:enterovirus /path/to/the/repo.git
		 * > chown: changing ownership of 'repo.git': Operation not permitted
		 */
//		UserPrincipal user = lookupService.lookupPrincipalByName("git");
//		view.setOwner(user);
	}
}
