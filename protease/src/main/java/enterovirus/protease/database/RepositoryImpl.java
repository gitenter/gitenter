package enterovirus.protease.database;

import java.io.IOException;
import java.util.Optional;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import enterovirus.protease.domain.*;

@Repository
class RepositoryImpl implements RepositoryRepository {

	@Autowired private RepositoryDatabaseRepository repositoryDbRepository;
	@Autowired private OrganizationRepository organizationRepository;
	
	public RepositoryBean findById(Integer id) throws IOException {
		
		Optional<RepositoryBean> repositories = repositoryDbRepository.findById(id); 
		
		if (!repositories.isPresent()) {
			throw new IOException ("Id is not correct!");
		}
		
		RepositoryBean repository = repositories.get();
		return repository;
	}
	
	@Transactional
	public RepositoryBean findByOrganizationNameAndRepositoryName(String organizationName, String repositoryName) throws IOException {
		
		/*
		 * TODO:
		 * Consider using JOIN FETCH in Hibernate to implement this method.
		 * (https://docs.jboss.org/hibernate/orm/3.3/reference/en-US/html/queryhql.html)
		 */
		
		OrganizationBean organization = organizationRepository.findByName(organizationName);
		Hibernate.initialize(organization.getRepositories());
		
		RepositoryBean repository = organization.findRepositoryByName(repositoryName);		
		return repository;
	}

	public RepositoryBean saveAndFlush(RepositoryBean repository) {
		return repositoryDbRepository.saveAndFlush(repository);
	}
}
