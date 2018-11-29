package com.gitenter.envelope.service.exception;

import com.gitenter.protease.dao.exception.InputNotExistException;

public class IdNotExistException extends InputNotExistException {

	private static final long serialVersionUID = 1L;
	
	public IdNotExistException(Class<? extends Object> klass, Integer id) {
		super("Id "+id+" for object "+klass+" is not existing yet");
	}
}