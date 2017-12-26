package enterovirus.protease.database;

import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;

import enterovirus.protease.domain.MemberBean;

public interface MemberRepository extends PagingAndSortingRepository<MemberBean, Integer> {

	List<MemberBean> findByUsername(String username);
	
	/* In Spring Data JPA, save() does both jobs of INSERT and UPDATE,
	 * depend on whether the primary key is the same or not. 
	 * 
	 * The difference between save() and saveAndFlush() is 
	 * saveAndFlush() will commit immediately, while save() will only 
	 * comment when commit() or flush(). */
	MemberBean saveAndFlush(MemberBean member);
}
