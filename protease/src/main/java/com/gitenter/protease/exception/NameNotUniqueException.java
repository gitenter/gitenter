package com.gitenter.protease.exception;

public class NameNotUniqueException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public NameNotUniqueException(String message) {
		super(message);
	}
}