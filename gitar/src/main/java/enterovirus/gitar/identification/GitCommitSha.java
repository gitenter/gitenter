package enterovirus.gitar.identification;

public class GitCommitSha {
	
	private String shaChecksumHash;
	
	public GitCommitSha (String shaChecksumHash) {
		this.shaChecksumHash = shaChecksumHash;
	}
	
	public String getShaChecksumHash () {
		return shaChecksumHash;
	}
}
