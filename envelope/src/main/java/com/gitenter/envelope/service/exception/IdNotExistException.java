package com.gitenter.envelope.service.exception;

import com.gitenter.protease.dao.exception.InputNotExistException;

public class IdNotExistException extends InputNotExistException {

	private static final long serialVersionUID = 1L;
	
	public IdNotExistException(String itemName, Integer id) {
		super("Id "+id+" for item "+itemName+" is not existing yet");
	}
}