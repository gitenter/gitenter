package com.gitenter.protease.dao.auth;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.persistence.PersistenceException;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gitenter.protease.dao.exception.RepositoryNameNotUniqueException;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.source.GitSource;

@Repository
class RepositoryRepositoryImpl implements RepositoryRepository {

	/*
	 * In the repository/DAO layer, we just bootstrap git content to
	 * the corresponding model itself (not include its linked components).
	 * 
	 * It actually can go done by EAGERly load components, but technically
	 * hard for LAZily load ones. Do not prefer that plan because that 
	 * builds so many dependencies of setups.
	 * 
	 * The other way is to override Hibernate's EAGER/LAZY loading part,
	 * to automatically not only generate SQL calls, but also git calls.
	 * Should need to hack Hibernate driver itself, so probably not easy.
	 * 
	 * Right now, the other GitUpdateFactories are loaded in the service
	 * layer. Don't know if it is better to load everything (include the
	 * one in here) to the service layer, which should then be refactored
	 * like we have two different DAO layers, one for SQL and one for 
	 * git, and combine them in a upper layer.
	 */
	@Autowired private RepositoryDatabaseRepository repositoryDatabaseRepository;
	@Autowired private RepositoryGitUpdateFactory repositoryGitUpdateFactory;
	
	@Autowired private GitSource gitSource;
	
	public Optional<RepositoryBean> findById(Integer id) {
		
		Optional<RepositoryBean> items = repositoryDatabaseRepository.findById(id); 
		
		if (items.isPresent()) {
			RepositoryBean item = items.get();
			repositoryGitUpdateFactory.update(item);
		}
		
		return items;
	}
	
	public List<RepositoryBean> findByOrganizationNameAndRepositoryName(String organizationName, String repositoryName) {
		
		List<RepositoryBean> items = repositoryDatabaseRepository.findByOrganizationNameAndRepositoryName(organizationName, repositoryName);
		
		for (RepositoryBean item : items) {
			repositoryGitUpdateFactory.update(item);
		}
		
		return items;
	}
	
	public RepositoryBean saveAndFlush(RepositoryBean repository) throws RepositoryNameNotUniqueException {
		try {
			return repositoryDatabaseRepository.saveAndFlush(repository);
		}
		catch (PersistenceException e) {
			/*
			 * TODO:
			 * Seems no easy way to catch, and/or read the inside exception 
			 * `org.hibernate.exception.ConstraintViolationException`, and
			 * then `org.postgresql.util.PSQLException`, and further distinguish
			 * different exceptions?
			 */
			if (e.getMessage().contains("ConstraintViolationException")) {
				throw new RepositoryNameNotUniqueException(repository);
			}
			throw e;
		}
	}

	@Override
	public void delete(RepositoryBean repository) throws IOException {
		repositoryDatabaseRepository.delete(repository);
		
		File repositoryDirectory = gitSource.getBareRepositoryDirectory(
				repository.getOrganization().getName(), 
				repository.getName());
		FileUtils.deleteDirectory(repositoryDirectory);
	}
}
