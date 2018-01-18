package enterovirus.gitar;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.RefSpec;
import org.junit.rules.TemporaryFolder;

public class GitRepository {
	
	static Repository getRepositoryFromDirectory(File repositoryDirectory) throws IOException {
		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(repositoryDirectory).readEnvironment().findGitDir().build();
		return repository;
	}
	
	static public void initBare (File repositoryDirectory, File sampleHooksDirectory) throws GitAPIException, IOException {
		
		Git.init().setDirectory(repositoryDirectory).setBare(true).call();
		
		/*
		 * Copy Git server-side hooks to the desired directory.
		 * 
		 * TODO:
		 * What about using symlink rather than physically copy
		 * the inside materials?
		 */
		FileUtils.copyDirectory(sampleHooksDirectory, new File(repositoryDirectory, "hooks"));
	}
	
	static public void initBareWithConfig (File repositoryDirectory, File sampleHooksDirectory, File configFilesDirectory) throws GitAPIException, IOException {
		
		initBare(repositoryDirectory, sampleHooksDirectory);
		
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
		}
	}
}
