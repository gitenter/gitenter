package com.gitenter.capsid.service.exception;

import com.gitenter.protease.domain.auth.MemberBean;

import lombok.Getter;

public class UsernameNotUniqueException extends NameNotUniqueException {

	private static final long serialVersionUID = 1L;
	
	@Getter
	private final String shortMessage = "Username already exist!";
	
	public UsernameNotUniqueException(MemberBean member) {
		super("Username "+member.getUsername()+" already exist.");
	}
}
