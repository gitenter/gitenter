package enterovirus.gitar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import enterovirus.gitar.GitBranch;
import enterovirus.gitar.GitTag;

public abstract class GitRepository {
	
	protected final File directory;
	
	/*
	 * TODO:
	 * This is mostly for bare repository only. Actions are through
	 * JGit "Git" class which implements "AutoCloseable" (so always
	 * need to stay in try blocks.
	 * 
	 * Consider at least to move this one to GitBareRepository, but
	 * still need a universal way to get JGit "git" and handle close()
	 * of it at the same time.
	 */
	Repository jGitRepository;

	public File getDirectory() {
		return directory;
	}
	
	public GitRepository(File directory) {
		this.directory = directory;
	}
	
	abstract Git getJGitGit() throws IOException;
	
	protected boolean isNormalRepository() {
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
	
	protected boolean isBareRepository() {
		/* 
		 * There is a git command `git rev-parse --is-bare-repository`
		 * but JGit does not implement it. Consider directory call that command.
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
	
	protected void buildJGitRepository() throws IOException {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		jGitRepository = builder.setGitDir(directory).readEnvironment().findGitDir().build();
	}
	
	public boolean isEmpty() throws IOException, GitAPIException {
		
		/*
		 * This is a hack, that a just initialized repository doesn't
		 * have any branch (even master does not exist).
		 * 
		 * TODO:
		 * Consider using other ways to decide if a repository is empty.
		 * For example, to check that no HEAD exists yet.
		 */
		if (getBranches().isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public GitCommit getCommit(String shaChecksumHash) throws IOException {
		return new GitCommit(this, shaChecksumHash);
	}

	public GitBranch getBranch(String branchName) throws IOException, CheckoutConflictException, GitAPIException {
		return new GitBranch(this, branchName);
	}
	
	public Collection<GitBranch> getBranches() throws IOException, GitAPIException {
		
		Collection<GitBranch> branches = new ArrayList<GitBranch>();
		try (Git git = getJGitGit()) {
			List<Ref> call = git.branchList().call();
			for (Ref ref : call) {
				/*
				 * Parse "refs/heads/master" and get the name "master".
				 * So for other branches.
				 */
				branches.add(new GitBranch(this, ref.getName().split("/")[2]));
			}
		}
		
		return branches;
	}
	
	public GitBranch getCurrentBranch() throws IOException {
		return new GitBranch(this, jGitRepository.getFullBranch());
	}
	
	public GitTag getTag(String tagName) throws IOException {
		return new GitTag(this, tagName);
	}
	
	public void addAHook(File filepath, String hookName) throws IOException {
		File hookFilepath = new File(getHooksDirectory(), hookName);
		FileUtils.copyFile(filepath, hookFilepath);
		hookFilepath.setExecutable(true);
	}
	
	public void addHooks(File folderpath) throws IOException {
		FileUtils.copyDirectory(folderpath, getHooksDirectory());
		for(File hookFile : getHooksDirectory().listFiles()) {
			hookFile.setExecutable(true);
		}
	}
	
	protected abstract File getHooksDirectory();
}
