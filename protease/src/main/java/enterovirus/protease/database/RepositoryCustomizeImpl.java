package enterovirus.protease.database;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import enterovirus.protease.domain.*;

@Repository
class RepositoryCustomizeImpl implements RepositoryRepository {

	@Autowired private RepositoryDatabaseRepository repositoryDbRepository;
	@Autowired private OrganizationRepository organizationRepository;
	
	public RepositoryBean findById(Integer id) throws IOException {
		
		Optional<RepositoryBean> repositories = repositoryDbRepository.findById(id); 
		
		if (!repositories.isPresent()) {
			throw new IOException ("Id is not correct!");
		}
		
		return repositories.get();
	}
	
	@Transactional
	public RepositoryBean findByOrganizationNameAndRepositoryName(String organizationName, String repositoryName) throws IOException {
		
		/*
		 * TODO:
		 * Consider using JOIN FETCH in Hibernate to implement this method.
		 * (https://docs.jboss.org/hibernate/orm/3.3/reference/en-US/html/queryhql.html)
		 */
		
		List<OrganizationBean> organizations = organizationRepository.findByName(organizationName);
		if (organizations.size() == 0) {
			throw new IOException ("Organization name is not correct!");
		}
		OrganizationBean organization = organizations.get(0);
		Hibernate.initialize(organization.getRepositories());
		
		RepositoryBean repository = organization.findRepositoryByName(repositoryName);
		return repository;
	}

	public RepositoryBean saveAndFlush(RepositoryBean repository) {
		return repositoryDbRepository.saveAndFlush(repository);
	}
}
