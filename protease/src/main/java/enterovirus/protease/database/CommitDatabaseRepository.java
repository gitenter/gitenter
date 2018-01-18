package enterovirus.protease.database;

import java.util.Optional;
import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;

import enterovirus.protease.domain.*;

interface CommitDatabaseRepository extends PagingAndSortingRepository<CommitBean, Integer> {

	Optional<CommitBean> findById(Integer id);
	List<CommitBean> findByShaChecksumHash(String shaChecksumHash);
	List<CommitBean> findByShaChecksumHashIn(List<String> shaChecksumHashs);
	
	public CommitBean saveAndFlush(CommitBean commit);
}
