package enterovirus.gitar.wrap;

import java.util.Date;

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

	public Date getCommitDate () {
		
		/*
		 * JGit getCommitTime(): time, expressed as seconds since the epoch.
		 * http://download.eclipse.org/jgit/docs/jgit-2.0.0.201206130900-r/apidocs/org/eclipse/jgit/revwalk/RevCommit.html#getCommitTime()
		 * 
		 * java.sql.Timestamp(long time): Constructs a Timestamp object using a milliseconds time value.
		 * https://docs.oracle.com/javase/8/docs/api/java/sql/Timestamp.html
		 * 
		 * So here need to do the transformation.
		 */
		return new Date(commitTime*1000L);
	}


	public String getFullMessage() {
		return fullMessage;
	}

	public CommitSha getCommitSha() {
		return new CommitSha(shaChecksumHash);
	}
}
