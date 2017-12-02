package enterovirus.capsid.database;

import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;

import enterovirus.capsid.domain.*;

/*
 * This interface is only used inside of this package by class
 * "DocumentImpl", so it doesn't need to be public.
 */
interface DocumentDatabaseRepository extends PagingAndSortingRepository<DocumentBean, Integer> {

	List<DocumentBean> findById(Integer id);
}
