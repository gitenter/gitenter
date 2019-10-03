package com.gitenter.capsid.service.exception;

import java.io.IOException;

public abstract class BackendException extends IOException {

	private static final long serialVersionUID = 1L;
	
	public BackendException(String message) {
		super(message);
	}
}