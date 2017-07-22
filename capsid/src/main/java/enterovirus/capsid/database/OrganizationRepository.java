package enterovirus.capsid.database;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

import enterovirus.capsid.domain.OrganizationBean;

public interface OrganizationRepository extends CrudRepository<OrganizationBean, Integer> {

	List<OrganizationBean> findByName(String name);
	OrganizationBean saveAndFlush(OrganizationBean member);
}
