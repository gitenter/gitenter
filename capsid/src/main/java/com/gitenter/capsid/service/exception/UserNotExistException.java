package com.gitenter.capsid.service.exception;

public class UserNotExistException extends ResourceNotFoundException {

	private static final long serialVersionUID = 1L;
	
	public UserNotExistException(String username) {
		super("User "+username+" is not existing yet");
	}
}