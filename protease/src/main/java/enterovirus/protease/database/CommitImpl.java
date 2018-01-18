package enterovirus.protease.database;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import enterovirus.protease.domain.*;
import enterovirus.gitar.*;
import enterovirus.gitar.wrap.BranchName;
import enterovirus.gitar.wrap.CommitSha;

@Repository
public class CommitImpl implements CommitRepository {

	@Autowired private CommitDatabaseRepository commitDbRepository;
	@Autowired private RepositoryRepository repositoryRepository;
	@Autowired private GitSource gitSource;
	
	public CommitBean findById(Integer id) throws IOException {
		
		Optional<CommitBean> commits = commitDbRepository.findById(id);
		
		if (!commits.isPresent()) {
			throw new IOException ("Commit id "+id+" is not correct!");
		}
		
		CommitBean commit = commits.get();
		return commit;
	}
	
	public CommitBean findByCommitSha(CommitSha commitSha) throws IOException {
		
		List<CommitBean> commits = commitDbRepository.findByShaChecksumHash(commitSha.getShaChecksumHash());
		
		if (commits.size() == 0) {
			throw new IOException ("SHA checksum hash "+commitSha.getShaChecksumHash()+" is not correct!");
		}
		if (commits.size() > 1) {
			throw new IOException ("SHA checksum hash "+commitSha.getShaChecksumHash()+" is not unique!");
		}
		
		CommitBean commit = commits.get(0);
		return commit;
	}
	
	public List<CommitBean> findByCommitShaIn(List<CommitSha> commitShas) {
		
		List<String> shaChecksumHashs = new ArrayList<String>();
		for (CommitSha commitSha : commitShas) {
			shaChecksumHashs.add(commitSha.getShaChecksumHash());
		}
		
		return commitDbRepository.findByShaChecksumHashIn(shaChecksumHashs);
	}
	
	public CommitBean findByRepositoryIdAndBranch(Integer repositoryId, BranchName branchName) throws IOException {

		/*
		 * TODO:
		 * Should be a better way rather than query the database twice?
		 */
		RepositoryBean repositoryBean = repositoryRepository.findById(repositoryId);
		String organizationName = repositoryBean.getOrganization().getName();
		String repositoryName = repositoryBean.getName();
		
		File repositoryDirectory = gitSource.getBareRepositoryDirectory(organizationName, repositoryName);
		
		/*
		 * TODO:
		 * Consider another gitar class that only get SHA but do not load the
		 * folder structure. So for commits which are not valid, there's no
		 * need to load the folder structure.
		 */
		GitFolderStructure gitFolderStructure = new GitFolderStructure(repositoryDirectory, branchName);
		
		String shaChecksumHash = gitFolderStructure.getCommitSha().getShaChecksumHash();
		List<CommitBean> commits = commitDbRepository.findByShaChecksumHash(shaChecksumHash);
		
		if (commits.size() == 0) {
			throw new IOException ("SHA checksum hash "+shaChecksumHash+" is not correct!");
		}
		if (commits.size() > 1) {
			throw new IOException ("SHA checksum hash "+shaChecksumHash+" is not unique!");
		}
		
		CommitBean commit = commits.get(0);
		
		if (commit instanceof CommitValidBean) {
			((CommitValidBean)commit).setFolderStructure(gitFolderStructure.getFolderStructure());
		}
		
		return commit;
	}
	
	public CommitBean saveAndFlush(CommitBean commit) {
		return commitDbRepository.saveAndFlush(commit);
	}
}
