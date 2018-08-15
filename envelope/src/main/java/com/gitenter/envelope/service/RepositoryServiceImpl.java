package com.gitenter.envelope.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gitenter.protease.dao.auth.RepositoryRepository;
import com.gitenter.protease.domain.auth.RepositoryBean;

@Service
public class RepositoryServiceImpl implements RepositoryService {
	
	@Autowired RepositoryRepository repositoryRepository;

	/*
	 * TODO:
	 * 
	 * User can only access repository information if she is authorized
	 * to do so.
	 */
	@Override
	public RepositoryBean getRepository(Integer repositoryId) {
		/*
		 * TODO:
		 * 
		 * Raise correct exception if the provided repositoryId doesn't exist.
		 */
		return repositoryRepository.findById(repositoryId).get();
	}
}
