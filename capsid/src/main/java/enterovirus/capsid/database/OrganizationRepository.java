package enterovirus.capsid.database;

import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import enterovirus.capsid.domain.OrganizationBean;

@RepositoryRestResource(collectionResourceRel="organizations", path="organizations")
public interface OrganizationRepository extends PagingAndSortingRepository<OrganizationBean, Integer> {

	List<OrganizationBean> findByName(@Param("name") String name);
	OrganizationBean saveAndFlush(OrganizationBean member);
}
