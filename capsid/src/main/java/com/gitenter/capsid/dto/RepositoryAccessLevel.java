package com.gitenter.capsid.dto;

import org.springframework.security.core.Authentication;

import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.auth.UserBean;

public enum RepositoryAccessLevel {

	READ,
	EDIT;
	
	public boolean canAccess(RepositoryBean repository, Authentication authentication) {
		
		if (repository.getIsPublic()) {
			switch(this) {
			case READ:
				return true;
				
			/*
			 * TODO:
			 * RepositoryAccessLevel.EDIT
			 */
			case EDIT:
				return false;
				
			default:
				return false;
			}
		}
		else {
			switch(this) {
			case READ:
				for (UserBean user : repository.getOrganization().getUsers()) {
					if (user.getUsername().equals(authentication.getName())) {
						return true;
					}
				}
				return false;
			
			/*
			 * TODO:
			 * RepositoryAccessLevel.EDIT
			 */
			case EDIT:
				return false;
				
			default:
				return false;
			}
		}
	}
}
