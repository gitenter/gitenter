package enterovirus.proteinsistence.database;

import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;

import enterovirus.proteinsistence.domain.*;

interface CommitDatabaseRepository extends PagingAndSortingRepository<CommitBean, Integer> {

	List<CommitBean> findById(Integer id);
	List<CommitBean> findByShaChecksumHash(String shaChecksumHash);
}
