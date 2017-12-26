package enterovirus.protease.database;

import java.util.Optional;
import org.springframework.data.repository.PagingAndSortingRepository;

import enterovirus.protease.domain.*;

/*
 * This interface is only used inside of this package by class
 * "DocumentImpl", so it doesn't need to be public.
 */
interface DocumentUnmodifiedDatabaseRepository extends PagingAndSortingRepository<DocumentUnmodifiedBean, Integer> {

	Optional<DocumentUnmodifiedBean> findById(Integer id);
}
