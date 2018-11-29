package com.gitenter.envelope.service.exception;

import com.gitenter.protease.dao.exception.InputNotExistException;

public class UserNotExistException extends InputNotExistException {

	private static final long serialVersionUID = 1L;
	
	public UserNotExistException(String username) {
		super("User "+username+" is not existing yet");
	}
}