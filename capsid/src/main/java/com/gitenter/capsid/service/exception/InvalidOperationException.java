package com.gitenter.capsid.service.exception;

public class InvalidOperationException extends FrontendException {

	private static final long serialVersionUID = 1L;
	
	public InvalidOperationException(String message) {
		super(message);
	}
}