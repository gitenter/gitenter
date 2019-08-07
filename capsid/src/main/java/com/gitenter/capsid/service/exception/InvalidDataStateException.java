package com.gitenter.capsid.service.exception;

public class InvalidDataStateException extends BackendException {

	private static final long serialVersionUID = 1L;
	
	public InvalidDataStateException(String message) {
		super(message);
	}
}