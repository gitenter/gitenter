package com.gitenter.protease.domain.auth;

import java.util.ArrayList;
import java.util.List;

import com.gitenter.protease.domain.Role;

import lombok.Getter;

@Getter
public enum RepositoryPersonRole implements Role {

	ORGANIZER('O', "Project organizer"),
	EDITOR('E', "Document editor"),
	BLACKLIST('B', "Blacklist");
	
	private Character shortName;
	private String displayName;

	private RepositoryPersonRole(Character shortName, String displayName) {
		this.shortName = shortName;
		this.displayName = displayName;
	}

	public static RepositoryPersonRole fromShortName(Character shortName) {
		switch (shortName) {
		case 'O':
			return ORGANIZER;
		case 'E':
			return EDITOR;
		case 'B':
			return BLACKLIST;
		
		default:
			throw new IllegalArgumentException("Repository person role shortName: "+shortName+" is not supported.");
		}
	}
	
	public static RepositoryPersonRole collaboratorRoleOf(String name) {
		RepositoryPersonRole role = valueOf(name);
		if (!role.equals(BLACKLIST)) {
			return role;
		}
		else {
			throw new IllegalArgumentException("Role "+name+" is not a collaborator role");
		}
	}
	
	public static List<RepositoryPersonRole> collaboratorRoles() {
		List<RepositoryPersonRole> roles = new ArrayList<RepositoryPersonRole>();
		for (RepositoryPersonRole role : values()) {
			if (!role.equals(BLACKLIST)) {
				roles.add(role);
			}
		}
		
		return roles;
	}
}
