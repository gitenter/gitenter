package enterovirus.gitar;

public abstract class GitPath {

	private final String relativePath;
	private final GitCommit commit;
	
	public GitPath(GitCommit commit, String relativePath) {
		this.commit = commit;
		this.relativePath = relativePath;
	}
}
