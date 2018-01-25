package enterovirus.protease.database;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import enterovirus.protease.domain.OrganizationBean;

interface OrganizationDatabaseRepository extends PagingAndSortingRepository<OrganizationBean, Integer> {

	Optional<OrganizationBean> findById(Integer id);
	List<OrganizationBean> findByName(String name);
	
	OrganizationBean saveAndFlush(OrganizationBean organization);
}
