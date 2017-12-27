package enterovirus.protease.database;

import java.io.IOException;

import enterovirus.protease.domain.*;

public interface RepositoryRepository {

	public RepositoryBean findById(Integer id) throws IOException;
	public RepositoryBean findByOrganizationNameAndRepositoryName(String organizationName, String repositoryName) throws IOException;
	
	public RepositoryBean saveAndFlush(RepositoryBean repository);
}
