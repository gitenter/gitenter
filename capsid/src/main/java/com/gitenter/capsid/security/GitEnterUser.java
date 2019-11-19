package com.gitenter.capsid.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.gitenter.protease.domain.auth.OrganizationUserRole;

public class GitEnterUser extends User implements UserDetails {

	private static final long serialVersionUID = 1L;

	private List<GrantedAuthority> authorities;
	
	public GitEnterUser(String username, String password) {
		super(username, password, new ArrayList<GrantedAuthority>());
		
		authorities = new ArrayList<GrantedAuthority>();
		
		/*
		 * TODO:
		 * Remove String-typed "SimpleGrantedAuthority" to a customized one.
		 */
		authorities.add(new SimpleGrantedAuthority(OrganizationUserRole.MANAGER.name()));
		authorities.add(new SimpleGrantedAuthority(OrganizationUserRole.MEMBER.name()));
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		return authorities;
	}
}
