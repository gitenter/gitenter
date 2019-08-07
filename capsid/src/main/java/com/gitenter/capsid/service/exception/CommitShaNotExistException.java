package com.gitenter.capsid.service.exception;

public class CommitShaNotExistException extends ResourceNotExistException {

	private static final long serialVersionUID = 1L;
	
	public CommitShaNotExistException(Integer repositoryId, String commitSha) {
		super("Commit SHA "+commitSha+" for repository "+repositoryId+" is not existing yet");
	}
}