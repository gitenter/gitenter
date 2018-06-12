package enterovirus.protease.database;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import enterovirus.protease.domain.*;

@Repository
class RepositoryImpl implements RepositoryRepository {

	@Autowired private RepositoryDatabaseRepository repositoryDbRepository;
	@Autowired private RepositoryGitDAO repositoryGitDAO;
	
	public RepositoryBean findById(Integer id) throws IOException {
		
		Optional<RepositoryBean> repositories = repositoryDbRepository.findById(id); 
		
		if (!repositories.isPresent()) {
			throw new IOException ("Id is not correct!");
		}
		
		RepositoryBean repository = repositories.get();
		return repository;
	}
	
	public RepositoryBean findByOrganizationNameAndRepositoryName(String organizationName, String repositoryName) throws IOException {
		
		List<RepositoryBean> repositories = repositoryDbRepository.findByOrganizationNameAndRepositoryName(organizationName, repositoryName);
		
		if (repositories.size() > 1) {
			throw new IOException ("Repository is not unique under organization name \""+organizationName+"\" and repository name \""+repositoryName+"\".");
		}
		else if (repositories.size() == 0) {
			throw new IOException ("Repository does not exist under organization name \""+organizationName+"\" and repository name \""+repositoryName+"\".");
		}
		
		return repositories.get(0);
	}
	
	public Collection<BranchBean> getBranches(RepositoryBean repository) throws IOException, GitAPIException {
		return repositoryGitDAO.getBranches(repository);
	}
	
	public List<CommitBean> getLog(RepositoryBean repository, BranchBean branch, Integer maxCount, Integer skip) throws IOException, GitAPIException {
		return repositoryGitDAO.getLog(repository, branch, maxCount, skip);
	}

	public RepositoryBean saveAndFlush(RepositoryBean repository) {
		return repositoryDbRepository.saveAndFlush(repository);
	}
}
