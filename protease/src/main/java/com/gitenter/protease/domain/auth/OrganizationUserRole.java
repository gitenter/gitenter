package com.gitenter.protease.domain.auth;

import com.gitenter.protease.domain.Role;

import lombok.Getter;

@Getter
public enum OrganizationUserRole implements Role {

	MANAGER('G', "Non-professional manager"),
	ORDINARY_MEMBER('M', "Ordinary member");
	
	private Character shortName;
	private String displayName;

	private OrganizationUserRole(Character shortName, String displayName) {
		this.shortName = shortName;
		this.displayName = displayName;
	}

	public static OrganizationUserRole fromShortName(Character shortName) {
		switch(shortName) {
		case 'G':
			return MANAGER;
		case 'M':
			return ORDINARY_MEMBER;
		
		default:
			throw new IllegalArgumentException("Organization user role shortName: "+shortName+" is not supported.");
		}
	}
}
