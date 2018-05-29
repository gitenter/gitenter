package enterovirus.gitar;

public class GitCommit {
	
	private final String shaChecksumHash;
	private final GitRepository repository;
	
	public GitCommit(GitRepository repository, String shaChecksumHash) {
		this.repository = repository;
		this.shaChecksumHash = shaChecksumHash;
	}

	public String getShaChecksumHash() {
		return shaChecksumHash;
	}

	public GitRepository getRepository() {
		return repository;
	}
}
