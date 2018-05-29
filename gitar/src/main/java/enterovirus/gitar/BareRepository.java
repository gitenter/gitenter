package enterovirus.gitar;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class BareRepository extends GitRepository {
	
	private Repository repository;
	
	Repository getRepository() {
		return repository;
	}

	/*
	 * TODO:
	 * Further wrap JGit exceptions.
	 */
	public BareRepository(File directory) throws IOException, GitAPIException {
		super(directory);
		
		if (!exists()) {
			Git.init().setDirectory(directory).setBare(true).call();
		}
		
		/*
		 * TODO:
		 * Can this part be used in exist()?
		 */
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		repository = builder.setGitDir(directory).readEnvironment().findGitDir().build();
	}
	
	private Boolean exists() {
		/*
		 * TODO: 
		 * What about the case if it is a broken git repo with only
		 * part of the files exist?
		 */
		if (!new File(directory, "branches").isDirectory()
				|| !new File(directory, "hooks").isDirectory()
				|| !new File(directory, "logs").isDirectory()
				|| !new File(directory, "objects").isDirectory()
				|| !new File(directory, "refs").isDirectory()
				|| !new File(directory, "config").isFile()
				|| !new File(directory, "HEAD").isFile()) {
			return false;
		}
		return true;
	}

	@Override
	public GitBranch getBranch(String branchName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<GitBranch> getBranches() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GitTag getTag(String tagName) {
		// TODO Auto-generated method stub
		return null;
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
