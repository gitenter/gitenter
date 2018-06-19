package com.gitenter.domain.auth;

import lombok.Getter;

@Getter
public enum RepositoryMemberRole {

	ORGANIZER('O', "Project organizer"),
	EDITOR('E', "Document editor"),
	REVIEWER('R', "Document reviewer"),
	BLACKLIST('B', "Blacklist");
	
	private Character shortName;
	private String displayName;

	private RepositoryMemberRole(Character shortName, String displayName) {
		this.shortName = shortName;
		this.displayName = displayName;
	}

	public static RepositoryMemberRole fromShortName(Character shortName) {
		switch (shortName) {
		case 'O':
			return ORGANIZER;
		case 'E':
			return EDITOR;
		case 'R':
			return REVIEWER;
		case 'B':
			return BLACKLIST;
		
		default:
			throw new IllegalArgumentException("Repository member role shortName: "+shortName+" is not supported.");
		}
	}
}
