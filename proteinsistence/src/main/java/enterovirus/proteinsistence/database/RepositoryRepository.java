package enterovirus.proteinsistence.database;

import java.io.IOException;

import enterovirus.proteinsistence.domain.*;

public interface RepositoryRepository {

	public RepositoryBean findById(Integer id) throws IOException;
	public RepositoryBean saveAndFlush(RepositoryBean repository);
}
