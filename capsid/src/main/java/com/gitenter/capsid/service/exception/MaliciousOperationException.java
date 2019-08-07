package com.gitenter.capsid.service.exception;

public class MaliciousOperationException extends UnreachableOperationException {

	private static final long serialVersionUID = 1L;

	public MaliciousOperationException(String message) {
		super(message);
	}
}