package enterovirus.protease.database;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import enterovirus.protease.domain.*;

/*
 * This interface is only used inside of this package by class
 * "DocumentImpl", so it doesn't need to be public.
 */
interface DocumentModifiedDatabaseRepository extends PagingAndSortingRepository<DocumentModifiedBean, Integer> {

	Optional<DocumentModifiedBean> findById(Integer id);
	List<DocumentModifiedBean> findByCommitIdAndRelativeFilepath(Integer commitId, String relativeFilepath);
}
