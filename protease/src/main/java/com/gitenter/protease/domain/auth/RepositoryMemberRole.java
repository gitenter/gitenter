package com.gitenter.protease.domain.auth;

import java.util.ArrayList;
import java.util.List;

import com.gitenter.protease.domain.Role;

import lombok.Getter;

@Getter
public enum RepositoryMemberRole implements Role {

	ORGANIZER('O', "Project organizer"),
	EDITOR('E', "Document editor"),
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
		case 'B':
			return BLACKLIST;
		
		default:
			throw new IllegalArgumentException("Repository member role shortName: "+shortName+" is not supported.");
		}
	}
	
	public static RepositoryMemberRole collaboratorRoleOf(String name) {
		RepositoryMemberRole role = valueOf(name);
		if (!role.equals(BLACKLIST)) {
			return role;
		}
		else {
			throw new IllegalArgumentException("Role "+name+" is not a collaborator role");
		}
	}
	
	public static List<RepositoryMemberRole> collaboratorRoles() {
		List<RepositoryMemberRole> roles = new ArrayList<RepositoryMemberRole>();
		for (RepositoryMemberRole role : values()) {
			if (!role.equals(BLACKLIST)) {
				roles.add(role);
			}
		}
		
		return roles;
	}
}
