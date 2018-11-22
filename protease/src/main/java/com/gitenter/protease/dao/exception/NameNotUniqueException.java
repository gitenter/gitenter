package com.gitenter.protease.dao.exception;

public class NameNotUniqueException extends InputNotUniqueException {

	private static final long serialVersionUID = 1L;
	
	public NameNotUniqueException(String message) {
		super(message);
	}
}