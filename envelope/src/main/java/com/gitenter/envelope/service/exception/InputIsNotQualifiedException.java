package com.gitenter.envelope.service.exception;

import com.gitenter.protease.dao.exception.InvalidInputException;

public class InputIsNotQualifiedException extends InvalidInputException {

	private static final long serialVersionUID = 1L;
	
	public InputIsNotQualifiedException(String message) {
		super(message);
	}
}