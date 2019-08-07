package com.gitenter.capsid.service.exception;

public class UnreachableOperationException extends FrontendException {

	private static final long serialVersionUID = 1L;
	
	public UnreachableOperationException(String message) {
		super(message);
	}
}