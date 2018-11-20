package com.gitenter.protease.dao.exception;

public abstract class InputNotExistException extends InvalidInputException {

	private static final long serialVersionUID = 1L;
	
	public InputNotExistException(String message) {
		super(message);
	}
}