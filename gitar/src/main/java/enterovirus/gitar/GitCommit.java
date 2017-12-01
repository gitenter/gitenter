package enterovirus.gitar;

public class GitCommit {
	
	private String shaChecksumHash;
	
	public GitCommit (String shaChecksumHash) {
		this.shaChecksumHash = shaChecksumHash;
	}
	
	String getShaChecksumHash () {
		return shaChecksumHash;
	}
}
