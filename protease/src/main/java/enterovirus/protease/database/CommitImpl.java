package enterovirus.protease.database;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gitenter.gitar.*;

import enterovirus.protease.domain.*;
import enterovirus.protease.source.GitSource;

@Repository
public class CommitImpl implements CommitRepository {

	@Autowired private CommitDatabaseRepository commitDbRepository;
	@Autowired private RepositoryRepository repositoryRepository;
	@Autowired private GitSource gitSource;
	
	public CommitBean findById(Integer id) throws IOException, GitAPIException {
		
		Optional<CommitBean> commits = commitDbRepository.findById(id);
		
		if (!commits.isPresent()) {
			throw new IOException ("Commit id "+id+" is not correct!");
		}
		
		CommitBean commit = commits.get();
		updateFromGit(commit);
		
		return commit;
	}
	
	public CommitBean findByRepositoryIdAndCommitSha(Integer repositoryId, String commitSha) throws IOException, GitAPIException {
		
		List<CommitBean> commits = commitDbRepository.findByRepositoryIdAndShaChecksumHash(repositoryId, commitSha);
		
		if (commits.size() == 0) {
			throw new IOException ("In repository No."+repositoryId+", SHA checksum hash "+commitSha+" is not correct!");
		}
		if (commits.size() > 1) {
			throw new IOException ("In repository No."+repositoryId+", SHA checksum hash "+commitSha+" is not unique!");
		}
		
		CommitBean commit = commits.get(0);
		updateFromGit(commit);
		
		return commit;
	}
	
	public List<CommitBean> findByRepositoryIdAndCommitShaIn(Integer repositoryId, List<String> commitShas) throws IOException, GitAPIException {
		
		List<CommitBean> commits = commitDbRepository.findByRepositoryIdAndShaChecksumHashIn(repositoryId, commitShas);
		
		/*
		 * TODO:
		 * This approach is not good, as it will query from git multiple times.
		 * think about a way that it can be done in one single git query.
		 */
		for (CommitBean commit : commits) {
			updateFromGit(commit);
		}
		
		return commits;
	}
	
	public CommitBean findByRepositoryIdAndBranch(Integer repositoryId, BranchBean branch) throws IOException, GitAPIException {

		/*
		 * TODO:
		 * Should be a better way rather than query the database twice?
		 */
		RepositoryBean repositoryBean = repositoryRepository.findById(repositoryId);
		File repositoryDirectory = gitSource.getBareRepositoryDirectory(
				repositoryBean.getOrganization().getName(), 
				repositoryBean.getName());
		
		GitRepository gitRepository = GitBareRepository.getInstance(repositoryDirectory);
		GitCommit gitCommit = gitRepository.getBranch(branch.getName()).getHead();
		
		List<CommitBean> commits = commitDbRepository.findByRepositoryIdAndShaChecksumHash(repositoryId, gitCommit.getSha());
		
		if (commits.size() == 0) {
			throw new IOException ("SHA checksum hash "+gitCommit.getSha()+" is not correct!");
		}
		if (commits.size() > 1) {
			throw new IOException ("SHA checksum hash "+gitCommit.getSha()+" is not unique!");
		}
		
		CommitBean commit = commits.get(0);
		updateFromGitCommit(commit, gitCommit);
		
		return commit;
	}
	
	private void updateFromGit(CommitBean commit) throws IOException, GitAPIException {
		
		File repositoryDirectory = gitSource.getBareRepositoryDirectory(
				commit.getRepository().getOrganization().getName(), 
				commit.getRepository().getName());
		
		GitRepository gitRepository = GitBareRepository.getInstance(repositoryDirectory);
		GitCommit gitCommit = gitRepository.getCommit(commit.getShaChecksumHash());
		
		updateFromGitCommit(commit, gitCommit);
	}
	
	private void updateFromGitCommit(CommitBean commit, GitCommit gitCommit) {
		
		commit.setTime(gitCommit.getTime());
		commit.setMessage(gitCommit.getMessage());
		commit.setAuthor(new AuthorBean(gitCommit.getAuthor()));
	}
	
	public CommitBean saveAndFlush(CommitBean commit) {
		return commitDbRepository.saveAndFlush(commit);
	}
}
