package com.gitenter.capsid.service.exception;

public class UnreachableException extends FrontendException {

	private static final long serialVersionUID = 1L;
	
	public UnreachableException(String message) {
		super(message);
	}
}