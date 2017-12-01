package enterovirus.gitar;

public class GitCommit {
	
	private String shaChecksumHash;
	
	public GitCommit (String shaChecksumHash) {
		this.shaChecksumHash = shaChecksumHash;
	}
	
	public String getShaChecksumHash () {
		return shaChecksumHash;
	}
}
