package enterovirus.protease.database;

import java.io.IOException;

import enterovirus.protease.domain.OrganizationBean;

public interface OrganizationRepository {
	
	public OrganizationBean findById(Integer id) throws IOException;
	public OrganizationBean findByName(String name) throws IOException;
	
	public OrganizationBean saveAndFlush(OrganizationBean organization);
}
