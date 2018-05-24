package enterovirus.protease.database;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import enterovirus.protease.domain.MmBean;

interface MmRepository extends PagingAndSortingRepository<MmBean, Integer> {

	Optional<MmBean> findById(Integer id);
	List<MmBean> findByUsername(String username);
	
	/* In Spring Data JPA, save() does both jobs of INSERT and UPDATE,
	 * depend on whether the primary key is the same or not. 
	 * 
	 * The difference between save() and saveAndFlush() is 
	 * saveAndFlush() will commit immediately, while save() will only 
	 * comment when commit() or flush(). */
	MmBean saveAndFlush(MmBean member);
}