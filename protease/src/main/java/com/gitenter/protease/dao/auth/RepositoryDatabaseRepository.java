package com.gitenter.protease.dao.auth;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.gitenter.protease.domain.auth.RepositoryBean;

interface RepositoryDatabaseRepository extends PagingAndSortingRepository<RepositoryBean, Integer> {

	Optional<RepositoryBean> findById(Integer id);
	@Query("select rp "
			+ "from RepositoryBean rp join rp.organization og "
			+ "where rp.name = :repositoryName and og.name = :organizationName")
	List<RepositoryBean> findByOrganizationNameAndRepositoryName(
			@Param("organizationName") String organizationName, 
			@Param("repositoryName") String repositoryName);
	
	RepositoryBean saveAndFlush(RepositoryBean repository);
}
