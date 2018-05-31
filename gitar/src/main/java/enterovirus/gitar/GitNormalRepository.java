package enterovirus.gitar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import enterovirus.gitar.GitBranch;
import enterovirus.gitar.GitTag;

public class GitNormalRepository extends GitRepository {
	
	private Map<String,GitRemote> remotes = new HashMap<String,GitRemote>();

	/*
	 * TODO:
	 * Further wrap JGit exceptions.
	 */
	public GitNormalRepository(File directory) throws IOException, GitAPIException {
		super(directory);
		
		if (isBareRepository()) {
			/*
			 * TODO:
			 * Consider throw a customized exception.
			 */
			throw new IOException("The provided directory is a bare git directory: "+directory);
		}
		else if (!isNormalRepository()) {
			Git.init().setDirectory(directory).setBare(false).call();
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
	
	public void addRemote(String name, String url) {
		
		remotes.put(name, new GitRemote(this, name, url));
		
//		StoredConfig config = jGitRepository.getConfig();
//		config.setString("remote", name, "url", url);
//		config.save();
	}
	
	public GitRemote getRemote(String name) {
		return remotes.get(name);
	}
	
	public void createBranch(String branchName) throws InvalidRefNameException, GitAPIException {
		try (Git git = new Git(jGitRepository)) {
			git.branchCreate().setName(branchName).call();
		}
	}
}
