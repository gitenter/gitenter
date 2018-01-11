package enterovirus.protease.database;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import enterovirus.gitar.GitBranch;
import enterovirus.gitar.GitLog;
import enterovirus.gitar.GitSource;
import enterovirus.gitar.wrap.BranchName;
import enterovirus.protease.domain.RepositoryBean;

/*
 * Functions of this class is not merged into RepositoryRepository,
 * is because some usage of RepositoryBean doesn't need the git data.
 * This makes some kind of lazy evaluation.
 * 
 * TODO:
 * Can it be done in the way that it loads the Git data when calling 
 * "repository.getCommitLog()" and "repository.getBranchNames()" ?
 */
@Component
public class RepositoryGitDAO {

	@Autowired private GitSource gitSource;
	
	public RepositoryBean loadCommitLog(RepositoryBean repository, BranchName branchName) throws IOException, GitAPIException {
		
		File repositoryDirectory = gitSource.getBareRepositoryDirectory(repository.getOrganization().getName(), repository.getName());
		GitLog gitLog = new GitLog(repositoryDirectory, branchName);
		repository.setCommitInfos(gitLog.getCommitInfos());
		
		return repository;
	}
	
	public RepositoryBean loadBranchNames (RepositoryBean repository) throws IOException, GitAPIException {
		
		File repositoryDirectory = gitSource.getBareRepositoryDirectory(repository.getOrganization().getName(), repository.getName());
		GitBranch gitBranch = new GitBranch(repositoryDirectory);
		repository.setBranchNames(gitBranch.getBranchNames());
		
		return repository;
	}

}
