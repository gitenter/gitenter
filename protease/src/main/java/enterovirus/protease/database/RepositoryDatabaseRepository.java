package enterovirus.protease.database;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import enterovirus.protease.domain.*;

interface RepositoryDatabaseRepository extends PagingAndSortingRepository<RepositoryBean, Integer> {

	Optional<RepositoryBean> findById(Integer id);
	RepositoryBean saveAndFlush(RepositoryBean repository);
}
