package com.gitenter.database.auth;

import java.io.IOException;

import com.gitenter.domain.auth.OrganizationBean;

public interface OrganizationRepository {
	
	public OrganizationBean findById(Integer id) throws IOException;
	public OrganizationBean findByName(String name) throws IOException;
	
	public OrganizationBean saveAndFlush(OrganizationBean organization);
}
