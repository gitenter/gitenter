package com.gitenter.protease.dao.exception;

public abstract class InputNotUniqueException extends InvalidInputException {

	private static final long serialVersionUID = 1L;
	
	public InputNotUniqueException(String message) {
		super(message);
	}
}