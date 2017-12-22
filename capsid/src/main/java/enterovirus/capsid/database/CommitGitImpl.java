package enterovirus.capsid.database;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import enterovirus.capsid.domain.*;
import enterovirus.gitar.*;
import enterovirus.gitar.wrap.BranchName;
import enterovirus.gitar.wrap.CommitSha;

@Repository
public class CommitGitImpl implements CommitRepository {

	@Autowired private CommitDatabaseRepository repository;
	@Autowired private RepositoryRepository repositoryRepository;
	@Autowired private GitSource gitSource;
	
	public CommitBean findById(Integer id) throws IOException {
		
		List<CommitBean> commits = repository.findById(id);
		
		if (commits.size() == 0) {
			throw new IOException ("Id is not correct!");
		}
		if (commits.size() > 1) {
			throw new IOException ("Id is not unique!");
		}
		
		CommitBean commit = commits.get(0);
		updateGitMaterial(commit);
		return commit;
	}
	
	public CommitBean findByShaChecksumHash(String shaChecksumHash) throws IOException {
		
		List<CommitBean> commits = repository.findByShaChecksumHash(shaChecksumHash);
		
		if (commits.size() == 0) {
			throw new IOException ("SHA checksum hash is not correct!");
		}
		if (commits.size() > 1) {
			throw new IOException ("SHA checksum hash is not unique!");
		}
		
		CommitBean commit = commits.get(0);
		updateGitMaterial(commit);
		return commit;
	}
	
	public CommitBean findByRepositoryIdAndBranch(Integer repositoryId, String branch) throws IOException {

		/*
		 * TODO:
		 * Should be a better way rather than query the database twice?
		 */
		RepositoryBean repositoryBean = repositoryRepository.findById(repositoryId).get(0);
		String organizationName = repositoryBean.getOrganization().getName();
		String repositoryName = repositoryBean.getName();
		
		File repositoryDirectory = gitSource.getBareRepositoryDirectory(organizationName, repositoryName);
		BranchName branchName = new BranchName(branch);
		
		GitCommit gitCommit = new GitCommit(repositoryDirectory, branchName);
		
		String shaChecksumHash = gitCommit.getCommitSha().getShaChecksumHash();
		List<CommitBean> commits = repository.findByShaChecksumHash(shaChecksumHash);
		
		if (commits.size() == 0) {
			throw new IOException ("SHA checksum hash is not correct!");
		}
		if (commits.size() > 1) {
			throw new IOException ("SHA checksum hash is not unique!");
		}
		
		CommitBean commit = commits.get(0);
		commit.setFolderStructure(gitCommit.getFolderStructure());
		return commit;
	}
	
	public CommitBean findByRepositoryId(Integer repositoryId) throws IOException {
		return findByRepositoryIdAndBranch(repositoryId, "master");
	}
	
	private CommitBean updateGitMaterial (CommitBean commit) throws IOException {
		
		String organizationName = commit.getRepository().getOrganization().getName();
		String repositoryName = commit.getRepository().getName();
		
		File repositoryDirectory = gitSource.getBareRepositoryDirectory(organizationName, repositoryName);
		CommitSha commitSha = new CommitSha(commit.getShaChecksumHash());
		
		GitCommit gitCommit = new GitCommit(repositoryDirectory, commitSha);
		
		commit.setFolderStructure(gitCommit.getFolderStructure());
		
		return commit;
	}
}
