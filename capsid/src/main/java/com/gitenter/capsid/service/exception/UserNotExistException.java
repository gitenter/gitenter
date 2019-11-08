package com.gitenter.capsid.service.exception;

public class UserNotExistException extends ResourceNotFoundException {

	private static final long serialVersionUID = 1L;
	
	public UserNotExistException(Integer memberId) {
		super("User with ID "+memberId+" is not existing yet");
	}
	
	public UserNotExistException(String username) {
		super("User "+username+" is not existing yet");
	}
}