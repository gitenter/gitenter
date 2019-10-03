package com.gitenter.capsid.service.exception;

public class MaliciousOperationException extends UnreachableException {

	private static final long serialVersionUID = 1L;

	public MaliciousOperationException(String message) {
		super(message);
	}
}