package com.gitenter.protease.dao.exception;

import java.io.IOException;

/*
 * TODO:
 * This is not a good place for this abstract classes.
 * Ideally we should put them in `com.gitenter.exception`.
 * But since `com.gitenter` is in several different maven 
 * projects, and there's no master maven project, we 
 * temporarily put them in here.
 */
public abstract class InvalidInputException extends IOException {

	private static final long serialVersionUID = 1L;
	
	public InvalidInputException(String message) {
		super(message);
	}
}