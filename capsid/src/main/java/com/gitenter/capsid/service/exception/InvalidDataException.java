package com.gitenter.capsid.service.exception;

import java.io.IOException;

public class InvalidDataException extends IOException {

	private static final long serialVersionUID = 1L;
	
	public InvalidDataException(String message) {
		super(message);
	}
}