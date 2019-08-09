package com.gitenter.capsid.service.exception;

public class NameNotUniqueException extends ConstraintViolationException {

	private static final long serialVersionUID = 1L;
	
	public NameNotUniqueException(String message) {
		super(message);
	}
}