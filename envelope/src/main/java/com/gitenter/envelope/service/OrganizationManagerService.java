package com.gitenter.envelope.service;

public interface OrganizationManagerService {

	public void addOrganizationMember(Integer organizationId, String username);
	public void removeOrganizationMember(Integer organizationId, String username);
	
	public void addOrganizationManager(Integer organizationId, String username);
	public void removeOrganizationManager(Integer organizationId, String username);
}
