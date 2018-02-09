package enterovirus.protease.database;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import enterovirus.protease.domain.RepositoryMemberMapBean;

public interface RepositoryMemberMapRepository extends CrudRepository<RepositoryMemberMapBean, Integer> {

	public Optional<RepositoryMemberMapBean> findById(Integer mapId);
	@Query("select rm "
			+ "from RepositoryMemberMapBean rm "
			+ "join rm.repository rp "
			+ "join rm.member mb "
			+ "where mb.username = :username and rp.id = :repositoryId")
	public List<RepositoryMemberMapBean> findByUsernameAndRepositoryId (
			@Param("username") String username,  
			@Param("repositoryId") Integer repositoryId);
	@Query("select rm "
			+ "from RepositoryMemberMapBean rm "
			+ "join rm.repository rp "
			+ "join rm.member mb "
			+ "join rp.organization og "
			+ "where mb.username = :username and rp.name = :repositoryName and og.name = :organizationName")
	public List<RepositoryMemberMapBean> findByUsernameAndOrganizationNameAndRepositoryName (
			@Param("username") String username, 
			@Param("organizationName") String organizationName, 
			@Param("repositoryName") String repositoryName);
	
	public void delete (RepositoryMemberMapBean map);
	public void deleteById (Integer mapId);
}
