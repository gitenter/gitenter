package enterovirus.gitar;

import java.io.IOException;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

public class GitCommit {
	
	private final String shaChecksumHash;
	final GitRepository repository;
	
	final RevCommit jGitCommit;
	
	public String getShaChecksumHash() {
		return shaChecksumHash;
	}
	
	/*
	 * Even if RevCommit is a subclass of ObjectId, we cannot remove objectId
	 * since they are raising different errors. 
	 * 
	 * For example, For GitTag.downCasting() to decide whether the tag is 
	 * annotated or not, by using jGitCommit annotated tags will raise the
	 * same error. Here we want to clear new ObjectId.
	 */
	protected ObjectId getObjectId() {
		return ObjectId.fromString(shaChecksumHash);
	}
	
	GitCommit(GitRepository repository, String shaChecksumHash) throws IOException {
		this.shaChecksumHash = shaChecksumHash;
		this.repository = repository;
		
		try (RevWalk revWalk = new RevWalk(repository.getJGitRepository())) {
			jGitCommit = revWalk.parseCommit(getObjectId());
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
