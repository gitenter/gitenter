package enterovirus.gitar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import enterovirus.gitar.wrap.CommitSha;

public class GitLog {

	private List<CommitSha> commitShas = new ArrayList<CommitSha>();
	
	public GitLog(File repositoryDirectory) throws IOException, GitAPIException {
		
		Repository repository = getRepositoryFromDirectory(repositoryDirectory);
		Git git = new Git(repository);
		Iterable<RevCommit> logs = git.log().call();
		buildCommitShas(logs);
	}
	
	private Repository getRepositoryFromDirectory(File repositoryDirectory) throws IOException {
		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(repositoryDirectory).readEnvironment().findGitDir().build();
		return repository;
	}
	
	private void buildCommitShas (Iterable<RevCommit> logs) {
		
		for (RevCommit rev : logs) {
			commitShas.add(new CommitSha(rev.getName()));
		}
	}

	public List<CommitSha> getCommitShas() {
		return commitShas;
	}
}
