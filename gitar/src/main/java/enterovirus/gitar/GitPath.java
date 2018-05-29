package enterovirus.gitar;

public abstract class GitPath {

	private final String relativePath;
	private final GitCommit gitCommit;
	
	public GitPath(GitCommit gitCommit, String relativePath) {
		this.gitCommit = gitCommit;
		this.relativePath = relativePath;
	}
}
