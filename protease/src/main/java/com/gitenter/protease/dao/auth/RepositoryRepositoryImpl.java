package com.gitenter.protease.dao.auth;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gitenter.protease.domain.auth.RepositoryBean;

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
	
	@Override
	public Optional<RepositoryBean> findById(Integer id) {
		
		Optional<RepositoryBean> items = repositoryDatabaseRepository.findById(id); 
		
		if (items.isPresent()) {
			RepositoryBean item = items.get();
			repositoryGitUpdateFactory.update(item);
		}
		
		return items;
	}
	
	@Override
	public List<RepositoryBean> findByOrganizationNameAndRepositoryName(String organizationName, String repositoryName) {
		
		List<RepositoryBean> items = repositoryDatabaseRepository.findByOrganizationNameAndRepositoryName(organizationName, repositoryName);
		
		for (RepositoryBean item : items) {
			repositoryGitUpdateFactory.update(item);
		}
		
		return items;
	}
	
	@Override
	public RepositoryBean init(RepositoryBean repository) throws IOException, GitAPIException {
		RepositoryBean savedRepository = repositoryDatabaseRepository.saveAndFlush(repository);
		repositoryGitUpdateFactory.create(repository);
		return savedRepository;
	}
	
	@Override
	public RepositoryBean update(RepositoryBean repository) {
		return repositoryDatabaseRepository.saveAndFlush(repository);
	}

	@Override
	public void delete(RepositoryBean repository) throws IOException, GitAPIException {
		repositoryDatabaseRepository.delete(repository);
		repositoryGitUpdateFactory.delete(repository);
	}
}
