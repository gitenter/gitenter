package com.gitenter.database.settings;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gitenter.domain.settings.OrganizationBean;

@Repository
public class OrganizationImpl implements OrganizationRepository {

	@Autowired private OrganizationDatabaseRepository organizationDbRepository;
	
	public OrganizationBean findById(Integer id) throws IOException {
		
		Optional<OrganizationBean> organizations = organizationDbRepository.findById(id);
		if (!organizations.isPresent()) {
			throw new IOException ("Organization id "+id+" does not exist!");
		}
		return organizations.get();
	}
	
	public OrganizationBean findByName(String name) throws IOException {
		
		List<OrganizationBean> organizations = organizationDbRepository.findByName(name);
		if (organizations.size() > 1) {
			throw new IOException ("Name "+name+" is not unique!");
		}
		else if (organizations.size() == 0) {
			throw new IOException ("Name "+name+" does not exist!");
		}
		
		return organizations.get(0);
	}

	public OrganizationBean saveAndFlush(OrganizationBean organization) {
		return organizationDbRepository.saveAndFlush(organization);
	}

}
