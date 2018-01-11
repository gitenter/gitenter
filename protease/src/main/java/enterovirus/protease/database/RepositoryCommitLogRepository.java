package enterovirus.protease.database;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import enterovirus.gitar.GitLog;
import enterovirus.gitar.GitSource;
import enterovirus.gitar.wrap.BranchName;
import enterovirus.protease.domain.RepositoryBean;

@Component
public class RepositoryCommitLogRepository {

	@Autowired private GitSource gitSource;
	
	public RepositoryBean loadCommitLog(RepositoryBean repository, BranchName branchName) throws IOException, GitAPIException {
		
		File repositoryDirectory = gitSource.getBareRepositoryDirectory(repository.getOrganization().getName(), repository.getName());
		
		GitLog gitLog = new GitLog(repositoryDirectory, branchName);
		
		repository.setCommitInfos(gitLog.getCommitInfos());
		
		return repository;
	}

}
