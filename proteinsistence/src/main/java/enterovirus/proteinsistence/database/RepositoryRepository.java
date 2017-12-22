package enterovirus.proteinsistence.database;

import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;

import enterovirus.proteinsistence.domain.*;

public interface RepositoryRepository extends PagingAndSortingRepository<RepositoryBean, Integer> {

	public List<RepositoryBean> findById(Integer id);
	RepositoryBean saveAndFlush(RepositoryBean repository);
}
