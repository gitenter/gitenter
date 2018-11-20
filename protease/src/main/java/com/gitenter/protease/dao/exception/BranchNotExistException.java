package com.gitenter.protease.dao.exception;

public class BranchNotExistException extends InputNotExistException {

	private static final long serialVersionUID = 1L;
	
	public BranchNotExistException(String branchName) {
		super("Branch "+branchName+" is not existing yet!");
	}
}