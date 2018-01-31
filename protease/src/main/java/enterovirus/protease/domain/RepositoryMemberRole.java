package enterovirus.protease.domain;

import lombok.Getter;

@Getter
public enum RepositoryMemberRole {

	READER('R'),
	REVIEWER('V'),
	EDITOR('E'),
	PROJECT_LEADER('L');
	
	private Character shortName;

	private RepositoryMemberRole(Character shortName) {
		this.shortName = shortName;
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
