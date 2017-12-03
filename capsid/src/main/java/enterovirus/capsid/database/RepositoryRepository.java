package enterovirus.capsid.database;

import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;

import enterovirus.capsid.domain.*;

public interface RepositoryRepository extends PagingAndSortingRepository<RepositoryBean, Integer> {

	public List<RepositoryBean> findById(Integer id);
}
