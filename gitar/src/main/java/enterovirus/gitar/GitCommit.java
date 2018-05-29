package enterovirus.gitar;

import java.io.IOException;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

public class GitCommit {
	
	private final String shaChecksumHash;
	final GitRepository gitRepository;
	
	private final ObjectId objectId;
	RevCommit commit;
	
	public String getShaChecksumHash() {
		return shaChecksumHash;
	}
	
	public GitCommit(GitRepository gitRepository, String shaChecksumHash) throws IOException {
		this.gitRepository = gitRepository;
		this.shaChecksumHash = shaChecksumHash;
		
		objectId = ObjectId.fromString(shaChecksumHash);
		
		try (RevWalk revWalk = new RevWalk(gitRepository.repository)) {
			commit = revWalk.parseCommit(objectId);
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
