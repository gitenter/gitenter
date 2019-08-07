package com.gitenter.capsid.service.exception;

public class ResourceNotExistException extends BackendException {

	private static final long serialVersionUID = 1L;
	
	public ResourceNotExistException(String message) {
		super(message);
	}
}