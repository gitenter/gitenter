package enterovirus.protease.database;

import org.springframework.data.repository.PagingAndSortingRepository;

import enterovirus.protease.domain.*;

public interface SshKeyDatabaseRepository extends PagingAndSortingRepository<SshKeyBean, Integer> {

	SshKeyBean saveAndFlush(SshKeyBean sshKey);
}
