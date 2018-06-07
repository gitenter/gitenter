package enterovirus.gitar;

import java.io.IOException;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

public class GitCommit {
	
	final GitRepository repository;
	
	protected final ObjectId objectId;
	final RevCommit jGitCommit;
	
	public String getShaChecksumHash() {
		return objectId.getName();
	}
	
	GitCommit(GitRepository repository, String shaChecksumHash) throws IOException {
		this.repository = repository;
		
		objectId = ObjectId.fromString(shaChecksumHash);
		try (RevWalk revWalk = new RevWalk(repository.getJGitRepository())) {
			jGitCommit = revWalk.parseCommit(objectId);
		}
	}
	
	/*
	 * TODO:
	 * To get a universal one which can automatically tell whether the relativePath
	 * is a folder or a file.
	 * > public GitPath getPath(String relativePath) throws IOException;
	 */
	public GitFile getFile(String relativePath) throws IOException {
		return new GitFile(this, relativePath);
	}
	
	public GitFolder getFolder(String relativePath) throws IOException {
		return new GitFolder(this, relativePath);
	}
}
