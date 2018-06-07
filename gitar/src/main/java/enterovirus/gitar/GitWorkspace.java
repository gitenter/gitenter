package enterovirus.gitar;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.RefSpec;

public class GitWorkspace extends File {
	
	private static final long serialVersionUID = 1L;
	
	private final GitBranch branch;
	private final GitNormalRepository repository;
	
	GitWorkspace(GitBranch branch, GitNormalRepository repository) {
		super(repository.directory.getAbsolutePath());
		
		this.branch = branch;
		this.repository = repository;
	}
	
	public void add(String pathspec) throws IOException, NoFilepatternException, GitAPIException {
		try (Git git = repository.getJGitGit()) {
			git.add().addFilepattern(pathspec).call();
		}
	}
	
	public void add() throws IOException, GitAPIException {
		add(".");
	}
	
	public void commit(String message) throws IOException, NoMessageException, GitAPIException {
		try (Git git = repository.getJGitGit()) {
			git.commit().setMessage(message).call();
		}		
	}
	
	public void push(GitRemote remote) throws IOException, TransportException, GitAPIException {
		
		assert repository.equals(remote.repository);
		
		try (Git git = repository.getJGitGit()) {
			/*
			 * TODO:
			 * The JGit logic in here is not the same as the git logic.
			 * 
			 * In git you setup a set of remote repositories by `git remote add` or 
			 * `git remote set-url` while defining a "name" of each remote repository,
			 * and push to the remote branch using that "name".
			 * 
			 * In JGit, the remote url itself is directly in use, so that kind of makes
			 * the remote name useless. However, then it is not clear why JGit still
			 * provide a setting page for which you can do `config.setString("remote", name, "url", url)`.
			 * Also, if we indeed go with the JGit approach, we can actually remove the
			 * name parameter in the GitRemote class.
			 */
			RefSpec spec = new RefSpec(branch.getName()+":"+branch.getName());
			git.push().setRemote(remote.url).setRefSpecs(spec).call();
		}
	}
}
