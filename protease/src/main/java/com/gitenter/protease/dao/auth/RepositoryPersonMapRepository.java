package com.gitenter.protease.dao.auth;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.gitenter.protease.domain.auth.RepositoryPersonMapBean;

public interface RepositoryPersonMapRepository extends CrudRepository<RepositoryPersonMapBean, Integer>, RepositoryPersonMapSql {

	public Optional<RepositoryPersonMapBean> findById(Integer mapId);
	@Query("select rm "
			+ "from RepositoryPersonMapBean rm "
			+ "join rm.repository rp "
			+ "join rm.person ps "
			+ "where ps.username = :username and rp.id = :repositoryId")
	public List<RepositoryPersonMapBean> findByUsernameAndRepositoryId(
			@Param("username") String username,  
			@Param("repositoryId") Integer repositoryId);
	@Query("select rm "
			+ "from RepositoryPersonMapBean rm "
			+ "join rm.repository rp "
			+ "join rm.person ps "
			+ "join rp.organization og "
			+ "where ps.username = :username and rp.name = :repositoryName and og.name = :organizationName")
	public List<RepositoryPersonMapBean> findByUsernameAndOrganizationNameAndRepositoryName(
			@Param("username") String username, 
			@Param("organizationName") String organizationName, 
			@Param("repositoryName") String repositoryName);
	
	public int throughSqlDeleteById(Integer mapId);
	
	public RepositoryPersonMapBean saveAndFlush(RepositoryPersonMapBean map);
}
