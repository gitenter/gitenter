package com.gitenter.capsid.service.exception;

import java.io.IOException;

public abstract class FrontendException extends IOException {

	private static final long serialVersionUID = 1L;
	
	public FrontendException(String message) {
		super(message);
	}
}