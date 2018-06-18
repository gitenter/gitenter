package com.gitenter.database.git;

import java.util.Optional;
import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.gitenter.domain.git.CommitBean;

public interface CommitDatabaseRepository extends PagingAndSortingRepository<CommitBean, Integer> {

	Optional<CommitBean> findById(Integer id);
	List<CommitBean> findByRepositoryIdAndShaChecksumHash(Integer repositoryId, String shaChecksumHash);
	List<CommitBean> findByRepositoryIdAndShaChecksumHashIn(Integer repositoryId, List<String> shaChecksumHashs);
	
	public CommitBean saveAndFlush(CommitBean commit);
}
