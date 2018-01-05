package enterovirus.protease.database;

import java.util.Optional;
import org.springframework.data.repository.PagingAndSortingRepository;

import enterovirus.protease.domain.*;

/*
 * This interface is only used inside of this package by class
 * "DocumentImpl", so it doesn't need to be public.
 */
interface DocumentDatabaseRepository extends PagingAndSortingRepository<DocumentBean, Integer> {

	Optional<DocumentBean> findById(Integer id);
	
	DocumentBean saveAndFlush(DocumentBean document);
}
