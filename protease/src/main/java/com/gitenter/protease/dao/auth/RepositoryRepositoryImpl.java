package com.gitenter.protease.dao.auth;

import java.util.List;
import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gitenter.protease.dao.exception.RepositoryNameNotUniqueException;
import com.gitenter.protease.domain.auth.RepositoryBean;

@Repository
class RepositoryRepositoryImpl implements RepositoryRepository {

	@Autowired private RepositoryDatabaseRepository repositoryDatabaseRepository;
	@Autowired private RepositoryGitUpdateFactory repositoryGitUpdateFactory;
	
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
}
