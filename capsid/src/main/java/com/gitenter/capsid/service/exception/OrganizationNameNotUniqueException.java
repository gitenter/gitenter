package com.gitenter.capsid.service.exception;

import com.gitenter.protease.domain.auth.OrganizationBean;

import lombok.Getter;

public class OrganizationNameNotUniqueException extends NameNotUniqueException {

	private static final long serialVersionUID = 1L;
	
	@Getter
	private final String shortMessage = "Organization name already exist!";
	
	public OrganizationNameNotUniqueException(OrganizationBean organization) {
		super("Organization name "+organization.getName()+" already exist.");
	}
}
