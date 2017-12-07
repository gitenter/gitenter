package enterovirus.capsid.database;

import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;

import enterovirus.capsid.domain.*;

interface CommitDatabaseRepository extends PagingAndSortingRepository<CommitBean, Integer> {

	List<CommitBean> findById(Integer id);
	List<CommitBean> findByShaChecksumHash(String shaChecksumHash);
}
