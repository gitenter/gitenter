package enterovirus.protease.database;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;

import enterovirus.protease.domain.*;

public interface RepositoryRepository {

	public RepositoryBean findById(Integer id) throws IOException;
	public RepositoryBean findByOrganizationNameAndRepositoryName(String organizationName, String repositoryName) throws IOException;
	
	public Collection<BranchBean> getBranches(RepositoryBean repository) throws IOException, GitAPIException;
	public List<CommitBean> getLog(RepositoryBean repository, BranchBean branch, Integer maxCount, Integer skip) throws IOException, GitAPIException;
	
	public RepositoryBean saveAndFlush(RepositoryBean repository);
}
