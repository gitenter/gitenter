package com.gitenter.protease.dao.git;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.gitenter.protease.domain.git.DocumentBean;
import com.gitenter.protease.domain.git.IncludeFileBean;

/*
 * This interface is only used inside of this package by class
 * "DocumentImpl", so it doesn't need to be public.
 */
interface IncludeFileDatabaseRepository extends PagingAndSortingRepository<IncludeFileBean, Integer> {

	Optional<IncludeFileBean> findById(Integer id);
	List<IncludeFileBean> findByCommitIdAndRelativePath(Integer commitId, String relativeFilepath);
	List<IncludeFileBean> findByCommitIdAndRelativePathIn(Integer commitId, List<String> relativeFilepaths);
	@Query("select file "
			+ "from IncludeFileBean file join file.commit cm "
			+ "where cm.sha = :sha and file.relativePath = :relativePath")
	List<IncludeFileBean> findByShaAndRelativePath(@Param("sha") String sha, @Param("relativePath") String relativePath);
	
	DocumentBean saveAndFlush(IncludeFileBean document);
}
