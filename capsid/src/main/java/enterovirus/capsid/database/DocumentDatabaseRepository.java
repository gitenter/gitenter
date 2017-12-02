package enterovirus.capsid.database;

import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;

import enterovirus.capsid.domain.*;

interface DocumentDatabaseRepository extends PagingAndSortingRepository<DocumentBean, Integer> {

	List<DocumentBean> findById(Integer id);
}
