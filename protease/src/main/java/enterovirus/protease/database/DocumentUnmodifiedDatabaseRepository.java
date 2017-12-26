package enterovirus.protease.database;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import enterovirus.protease.domain.*;

/*
 * This interface is only used inside of this package by class
 * "DocumentImpl", so it doesn't need to be public.
 */
interface DocumentUnmodifiedDatabaseRepository extends PagingAndSortingRepository<DocumentUnmodifiedBean, Integer> {

	@Query("select du "
			+ "from DocumentUnmodifiedBean du join du.originalDocument dm join du.commit cm "
			+ "where dm.relativeFilepath = :relativeFilepath and cm.id = :commitId")
	List<DocumentUnmodifiedBean> findByCommitIdAndRelativeFilepath(@Param("commitId") Integer commitId, @Param("relativeFilepath") String relativeFilepath);
}
