package com.gitenter.capsid.service.exception;

public class IdNotExistException extends ResourceNotFoundException {

	private static final long serialVersionUID = 1L;
	
	public IdNotExistException(Class<? extends Object> klass, Integer id) {
		super("Id "+id+" for domain object "+klass+" is not existing yet");
	}
}