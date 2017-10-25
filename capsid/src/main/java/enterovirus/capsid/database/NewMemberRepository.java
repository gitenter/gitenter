package enterovirus.capsid.database;

import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import enterovirus.capsid.domain.NewMemberBean;

@RepositoryRestResource(collectionResourceRel="members", path="members")
public interface NewMemberRepository extends PagingAndSortingRepository<NewMemberBean, Integer> {

	NewMemberBean saveAndFlush(NewMemberBean member);
}
