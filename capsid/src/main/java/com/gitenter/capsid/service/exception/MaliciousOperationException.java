package com.gitenter.capsid.service.exception;

public class MaliciousOperationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MaliciousOperationException(String message) {
		super(message);
	}
}