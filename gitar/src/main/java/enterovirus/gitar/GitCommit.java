package enterovirus.gitar;

import java.io.IOException;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

public class GitCommit {
	
	/* 
	 * TODO:
	 * Consider another Java class for date/time operations? 
	 */
	private final int time;
	private final String message;
	private final String shaChecksumHash;
	
	/*
	 * TODO:
	 * Add commit user information.
	 */
	
	final GitRepository repository;
	
	final RevCommit jGitCommit;
	
	public int getTime() {
		return time;
	}

	public String getMessage() {
		return message;
	}
	
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
		try (RevWalk revWalk = new RevWalk(repository.getJGitRepository())) {
			jGitCommit = revWalk.parseCommit(getObjectId());
			
			this.time = jGitCommit.getCommitTime();
			this.message = jGitCommit.getFullMessage();
		}
		
		this.repository = repository;
	}
	
	GitCommit (GitRepository repository, RevCommit jGitCommit) {
		
		this.time = jGitCommit.getCommitTime();
		this.message = jGitCommit.getFullMessage();
		this.shaChecksumHash = jGitCommit.getName();
		
		this.repository = repository;
		
		this.jGitCommit = jGitCommit;
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
