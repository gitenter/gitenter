package enterovirus.gitar.wrap;

import java.sql.Timestamp;

import org.eclipse.jgit.revwalk.RevCommit;

public class CommitInfo {
	
	private GitUserInfo gitUserInfo;
	private int commitTime; /* Consider another Java class for date/time operations? */
	private String fullMessage;
	private String shaChecksumHash;
	
	public CommitInfo(RevCommit revCommit) {
		this.gitUserInfo = new GitUserInfo(revCommit.getAuthorIdent());
		this.commitTime = revCommit.getCommitTime();
		this.fullMessage = revCommit.getFullMessage();
		this.shaChecksumHash = revCommit.getName();
	}

	public GitUserInfo getGitUserInfo() {
		return gitUserInfo;
	}
	
	public Timestamp getCommitTimestamp () {
		return new Timestamp(commitTime);
	}


	public String getFullMessage() {
		return fullMessage;
	}

	public CommitSha getShaChecksumHash() {
		return new CommitSha(shaChecksumHash);
	}
}
