package enterovirus.gitar;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

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
}
