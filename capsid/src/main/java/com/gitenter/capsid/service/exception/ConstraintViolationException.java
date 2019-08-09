package com.gitenter.capsid.service.exception;

public class ConstraintViolationException extends BackendException {

	private static final long serialVersionUID = 1L;
	
	public ConstraintViolationException(String message) {
		super(message);
	}
}