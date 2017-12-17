package enterovirus.capsid.database;

import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import enterovirus.capsid.domain.OrganizationBean;

/*
 * Notice that the HAL-formatted API also support PUT, PATCH and DELETE:
 * 
 * curl -X PUT -H "Content-Type:applicationjson" -d "{ \"name\": \"gov\", \"displayName\": \"govgov\" }" http://localhost:8888/hal/organizations/1
 */
@RepositoryRestResource(collectionResourceRel="organizations", path="organizations")
public interface OrganizationRepository extends PagingAndSortingRepository<OrganizationBean, Integer> {

	public List<OrganizationBean> findById(Integer id);
	public List<OrganizationBean> findByName(String name);
	public OrganizationBean saveAndFlush(OrganizationBean organization);
}
