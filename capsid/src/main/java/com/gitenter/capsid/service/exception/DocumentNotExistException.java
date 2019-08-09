package com.gitenter.capsid.service.exception;

public class DocumentNotExistException extends ResourceNotFoundException {

	private static final long serialVersionUID = 1L;
	
	public DocumentNotExistException(String commitSha, String relativePath) {
		super("Document "+relativePath+" doesn't not exist within commit SHA "+commitSha+".");
	}
	
	public DocumentNotExistException(Integer repositoryId, String branchName, String relativePath) {
		super("Document "+relativePath+" doesn't not exist within repository "+repositoryId+" and branch "+branchName+".");
	}
}