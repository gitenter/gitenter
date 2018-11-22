package com.gitenter.envelope.service.exception;

import com.gitenter.protease.dao.exception.InputNotExistException;

public class CommitShaNotExistException extends InputNotExistException {

	private static final long serialVersionUID = 1L;
	
	public CommitShaNotExistException(Integer repositoryId, String commitSha) {
		super("Commit SHA "+commitSha+" for repository "+repositoryId+" is not existing yet");
	}
}