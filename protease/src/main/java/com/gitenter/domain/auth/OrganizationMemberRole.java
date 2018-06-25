package com.gitenter.domain.auth;

import lombok.Getter;

@Getter
public enum OrganizationMemberRole {

	MANAGER('G', "Non-professional manager"),
	MEMBER('M', "Ordinary member");
	
	private Character shortName;
	private String displayName;

	private OrganizationMemberRole(Character shortName, String displayName) {
		this.shortName = shortName;
		this.displayName = displayName;
	}

	public static OrganizationMemberRole fromShortName(Character shortName) {
		switch (shortName) {
		case 'G':
			return MANAGER;
		case 'M':
			return MEMBER;
		
		default:
			throw new IllegalArgumentException("Organization member role shortName: "+shortName+" is not supported.");
		}
	}
}