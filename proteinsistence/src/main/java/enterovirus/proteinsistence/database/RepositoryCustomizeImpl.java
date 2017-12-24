package enterovirus.proteinsistence.database;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import enterovirus.proteinsistence.domain.RepositoryBean;

@Repository
class RepositoryCustomizeImpl implements RepositoryRepository {

	@Autowired private RepositoryDatabaseRepository repositoryDbRepository;
	
	public RepositoryBean findById(Integer id) throws IOException {
		
		List<RepositoryBean> repositories = repositoryDbRepository.findById(id); 
		
		if (repositories.size() == 0) {
			throw new IOException ("Id is not correct!");
		}
		if (repositories.size() > 1) {
			throw new IOException ("Id is not unique!");
		}
		
		return repositories.get(0);
	}

	public RepositoryBean saveAndFlush(RepositoryBean repository) {
		return repositoryDbRepository.saveAndFlush(repository);
	}
}
