package enterovirus.gitar.wrap;

public class CommitSha {
	
	private String shaChecksumHash;
	
	public CommitSha (String shaChecksumHash) {
		this.shaChecksumHash = shaChecksumHash;
	}
	
	public String getShaChecksumHash () {
		return shaChecksumHash;
	}
}
