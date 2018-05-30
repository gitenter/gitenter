package enterovirus.gitar;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import enterovirus.gitar.GitBranch;
import enterovirus.gitar.GitTag;

public class GitBareRepository extends GitRepository {

	/*
	 * TODO:
	 * Further wrap JGit exceptions.
	 */
	public GitBareRepository(File directory) throws IOException, GitAPIException {
		super(directory);
		
		if (isNormalRepository()) {
			/*
			 * TODO:
			 * Consider throw a customized exception.
			 */
			throw new IOException("The provided directory is a normal git directory: "+directory);
		}
		else if (!isBareRepository()) {
			Git.init().setDirectory(directory).setBare(true).call();
		}
		
		buildJGitRepository();
	}
	
	@Override
	public boolean addHook(File filepath) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addHooks(File folderpath) {
		// TODO Auto-generated method stub
		return false;
	}
}
