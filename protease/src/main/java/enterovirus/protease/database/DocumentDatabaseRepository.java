package enterovirus.protease.database;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import enterovirus.protease.domain.*;

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
			+ "where cm.shaChecksumHash = :shaChecksumHash and dc.relativeFilepath = :relativeFilepath")
	List<DocumentBean> findByShaChecksumHashAndRelativeFilepath(@Param("shaChecksumHash") String shaChecksumHash, @Param("relativeFilepath") String relativeFilepath);
	
	DocumentBean saveAndFlush(DocumentBean document);
}
