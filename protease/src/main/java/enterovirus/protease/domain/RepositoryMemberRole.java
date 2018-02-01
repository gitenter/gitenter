package enterovirus.protease.domain;

import lombok.Getter;

@Getter
public enum RepositoryMemberRole {

	READER('R', "Reader"),
	REVIEWER('V', "Reviewer"),
	EDITOR('E', "Editor"),
	PROJECT_LEADER('L', "Project Leader");
	
	private Character shortName;
	private String displayName;

	private RepositoryMemberRole(Character shortName, String displayName) {
		this.shortName = shortName;
		this.displayName = displayName;
	}

	public static RepositoryMemberRole fromShortName(Character shortName) {
		switch (shortName) {
		case 'R':
			return READER;
		case 'V':
			return REVIEWER;
		case 'E':
			return EDITOR;
		case 'L':
			return PROJECT_LEADER;
		
		default:
			throw new IllegalArgumentException("Repository member role shortName: "+shortName+" is not supported.");
		}
	}
}
