package com.gitenter.dao.git;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.gitenter.domain.git.DocumentBean;

/*
 * This interface is only used inside of this package by class
 * "DocumentImpl", so it doesn't need to be public.
 */
interface DocumentDatabaseRepository extends PagingAndSortingRepository<DocumentBean, Integer> {

	Optional<DocumentBean> findById(Integer id);
	List<DocumentBean> findByCommitIdAndRelativeFilepath(Integer commitId, String relativeFilepath);
	List<DocumentBean> findByCommitIdAndRelativeFilepathIn(Integer commitId, List<String> relativeFilepaths);
	@Query("select dc "
			+ "from DocumentBean dc join dc.commit cm "
			+ "where cm.sha = :sha and dc.relativeFilepath = :relativeFilepath")
	List<DocumentBean> findByShaAndRelativeFilepath(@Param("sha") String sha, @Param("relativeFilepath") String relativeFilepath);
	
	DocumentBean saveAndFlush(DocumentBean document);
}
