package com.gitenter.protease.dao.auth;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.gitenter.protease.domain.auth.RepositoryUserMapBean;

public interface RepositoryUserMapRepository extends CrudRepository<RepositoryUserMapBean, Integer>, RepositoryUserMapSql {

	public Optional<RepositoryUserMapBean> findById(Integer mapId);
	@Query("select rm "
			+ "from RepositoryUserMapBean rm "
			+ "join rm.repository rp "
			+ "join rm.user ur "
			+ "where ur.username = :username and rp.id = :repositoryId")
	public List<RepositoryUserMapBean> findByUsernameAndRepositoryId(
			@Param("username") String username,  
			@Param("repositoryId") Integer repositoryId);
	@Query("select rm "
			+ "from RepositoryUserMapBean rm "
			+ "join rm.repository rp "
			+ "join rm.user ur "
			+ "join rp.organization og "
			+ "where ur.username = :username and rp.name = :repositoryName and og.name = :organizationName")
	public List<RepositoryUserMapBean> findByUsernameAndOrganizationNameAndRepositoryName(
			@Param("username") String username, 
			@Param("organizationName") String organizationName, 
			@Param("repositoryName") String repositoryName);
	
	public int throughSqlDeleteById(Integer mapId);
	
	public RepositoryUserMapBean saveAndFlush(RepositoryUserMapBean map);
}
