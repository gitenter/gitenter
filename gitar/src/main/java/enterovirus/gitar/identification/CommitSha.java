package enterovirus.gitar.identification;

public class CommitSha {
	
	private String shaChecksumHash;
	
	public CommitSha (String shaChecksumHash) {
		this.shaChecksumHash = shaChecksumHash;
	}
	
	public String getShaChecksumHash () {
		return shaChecksumHash;
	}
}
