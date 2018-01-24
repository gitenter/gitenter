package enterovirus.gitar;

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
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.RefSpec;

public class GitRepository {
	
	static Repository getRepositoryFromDirectory(File repositoryDirectory) throws IOException {
		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(repositoryDirectory).readEnvironment().findGitDir().build();
		return repository;
	}
	
	static public void initBare (File repositoryDirectory, File sampleHooksDirectory) throws GitAPIException, IOException {
		
		initGitBareDefault(repositoryDirectory);
		setupHooks(repositoryDirectory, sampleHooksDirectory);
	}
	
	static public void initBareWithConfig (File repositoryDirectory, File sampleHooksDirectory, File configFilesDirectory) throws GitAPIException, IOException {
		
		initGitBareDefault(repositoryDirectory);
		
		/*
		 * TODO:
		 * Seems cannot successfully delete the file yet.
		 */
		File localRepositoryDirectory = Files.createTempDirectory("git-repo-").toFile();
		localRepositoryDirectory.deleteOnExit();
		
		try (Git git = Git.cloneRepository()
				.setURI(repositoryDirectory.getAbsolutePath())
				.setDirectory(localRepositoryDirectory).call()) {
		
			FileUtils.copyDirectory(configFilesDirectory, localRepositoryDirectory);
			git.add().addFilepattern(".").call();
			git.commit().setMessage("Initialization with system setup files.").call();
			
			String branch = "master";
			RefSpec spec = new RefSpec(branch + ":" + branch);
			git.push().setRemote(repositoryDirectory.getAbsolutePath()).setRefSpecs(spec).call();
			/*
			 * TODO:
			 * 
			 * First commit need to go BEFORE setup hooks, because otherwise "git push"
			 * will trigger the post-receive hook, but the repository row doesn't exist
			 * in SQL yet. The hook then raise error (I haven't see the error yet, because
			 * it is neither in capsid console or terminal console).
			 * 
			 * However, in this way we can only write to the database manually.
			 * That need to use protease, but gitar doesn't depend on protease. So currently
			 * we can only do it through AdminController rather than GitRepository.
			 * 
			 * It is really dirty. Consider a better way to handle that.
			 */
		}
		
		setupHooks(repositoryDirectory, sampleHooksDirectory);
	}
	
	static private void initGitBareDefault (File repositoryDirectory) throws IOException, GitAPIException {
		
		Git.init().setDirectory(repositoryDirectory).setBare(true).call();
		
		Set<PosixFilePermission> perms = new HashSet<>();
		perms.add(PosixFilePermission.OWNER_READ);
		perms.add(PosixFilePermission.OWNER_WRITE);
		perms.add(PosixFilePermission.OWNER_EXECUTE);
		perms.add(PosixFilePermission.GROUP_READ);
		perms.add(PosixFilePermission.GROUP_WRITE);
		perms.add(PosixFilePermission.GROUP_EXECUTE);
		perms.add(PosixFilePermission.OTHERS_READ);
		perms.add(PosixFilePermission.OTHERS_EXECUTE);
		
		UserPrincipalLookupService lookupService = FileSystems.getDefault().getUserPrincipalLookupService();
		GroupPrincipal group = lookupService.lookupPrincipalByGroupName("enterovirus");
		
		iterateFiles(repositoryDirectory, perms, group);
	}
	
	static private void iterateFiles (File directory, Set<PosixFilePermission> perms, GroupPrincipal group) throws IOException {
		
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
		for (File file : directory.listFiles()) {
			
			if (file.isDirectory()) {
				iterateFiles(file, perms, group);
			}
			
			/*
			 * chmod -R 775 /path/to/the/directory
			 */
			Files.setPosixFilePermissions(file.toPath(), perms);
			
			/*
			 * chown original-owner:enterovirus /path/to/the/directory
			 */
			PosixFileAttributeView view = Files.getFileAttributeView(file.toPath(), PosixFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
			view.setGroup(group);
		}
	}
	
	static private void setupHooks (File repositoryDirectory, File sampleHooksDirectory) throws IOException {
		
		/*
		 * Copy Git server-side hooks to the desired directory.
		 * 
		 * TODO:
		 * What about using symlink rather than physically copy
		 * the inside materials?
		 */
		File hookDirectory = new File(repositoryDirectory, "hooks");
		FileUtils.copyDirectory(sampleHooksDirectory, hookDirectory);
		
		/*
		 * See the following link for a list of possible server side hooks:
		 * https://git-scm.com/docs/githooks
		 * 
		 * In here, I just set them all. If some is not needed, we can just
		 * write blank in the corresponding hook.
		 */
		new File(hookDirectory, "pre-receive").setExecutable(true);
		new File(hookDirectory, "update").setExecutable(true);
		new File(hookDirectory, "post-receive").setExecutable(true);
		new File(hookDirectory, "post-update").setExecutable(true);
	}
}
