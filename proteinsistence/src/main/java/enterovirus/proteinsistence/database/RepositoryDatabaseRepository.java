package enterovirus.proteinsistence.database;

import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;

import enterovirus.proteinsistence.domain.*;

interface RepositoryDatabaseRepository extends PagingAndSortingRepository<RepositoryBean, Integer> {

	List<RepositoryBean> findById(Integer id);
	RepositoryBean saveAndFlush(RepositoryBean repository);
}
