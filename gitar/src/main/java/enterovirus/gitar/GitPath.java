package enterovirus.gitar;

public abstract class GitPath {

	private final String relativePath;
	private final GitCommit commit;
	
	protected GitPath(GitCommit commit, String relativePath) {
		this.commit = commit;
		this.relativePath = relativePath;
	}
}
