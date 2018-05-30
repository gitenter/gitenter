package enterovirus.gitar;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import enterovirus.gitar.GitBranch;
import enterovirus.gitar.GitTag;

public class GitLocalRepository extends GitRepository {

	/*
	 * TODO:
	 * Further wrap JGit exceptions.
	 */
	public GitLocalRepository(File directory) throws IOException, GitAPIException {
		super(directory);
		
		/*
		 * TODO:
		 * Check if it is the client side normal repository. If yes, should not
		 * setup the bare repository like this.
		 */
		if (!exists()) {
			Git.init().setDirectory(directory).setBare(false).call();
		}
		
		/*
		 * TODO:
		 * Can this part be used in exist()?
		 */
		buildJGitRepository();
	}
	
	private Boolean exists() {
		/*
		 * TODO: 
		 * What about the case if it is a broken git repo with only
		 * part of the files exist?
		 */
		if (!new File(directory, ".git").isDirectory()) {
			return false;
		}
		return true;
	}

	@Override
	public Boolean addHook(File filepath) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean addHooks(File folderpath) {
		// TODO Auto-generated method stub
		return null;
	}
}
