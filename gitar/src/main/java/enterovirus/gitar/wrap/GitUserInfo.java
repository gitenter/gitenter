package enterovirus.gitar.wrap;

import org.eclipse.jgit.lib.PersonIdent;

public class GitUserInfo {
	
	private String name;
	private String emailAddress;
	
	public GitUserInfo(PersonIdent personIdent) {
		this.name = personIdent.getName();
		this.emailAddress = personIdent.getEmailAddress();
	}

	public String getName() {
		return name;
	}

	public String getEmailAddress() {
		return emailAddress;
	}
}
