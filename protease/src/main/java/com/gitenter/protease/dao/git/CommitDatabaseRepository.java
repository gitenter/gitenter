package com.gitenter.protease.dao.git;

import java.util.Optional;
import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.gitenter.protease.domain.git.CommitBean;

public interface CommitDatabaseRepository extends PagingAndSortingRepository<CommitBean, Integer> {

	Optional<CommitBean> findById(Integer id);
	List<CommitBean> findByRepositoryIdAndSha(Integer repositoryId, String sha);
	List<CommitBean> findByRepositoryIdAndShaIn(Integer repositoryId, List<String> sha);
	
	public CommitBean saveAndFlush(CommitBean commit);
}
