package enterovirus.gihook.postreceive;

import java.util.List;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import enterovirus.gihook.postreceive.status.CommitStatus;
import enterovirus.gitar.GitDocument;
import enterovirus.gitar.GitFolderStructure;
import enterovirus.gitar.GitLog;
import enterovirus.gitar.wrap.CommitInfo;
import enterovirus.protease.database.CommitRepository;
import enterovirus.protease.database.RepositoryRepository;
import enterovirus.protease.domain.CommitBean;
import enterovirus.protease.domain.DocumentBean;
import enterovirus.protease.domain.DocumentModifiedBean;
import enterovirus.protease.domain.RepositoryBean;

@Service
public class UpdateGitCommit {

	@Autowired private RepositoryRepository repositoryRepository;
	@Autowired private CommitRepository commitRepository;
	
	/*
	 * TODO:
	 * Cannot do "private". Otherwise cannot initialize lazy evaluation of "commits".
	 * Don't understand why.
	 * 
	 * TODO:
	 * Move the relevant functions to some other classes, such as some controllers. 
	 */
	@Transactional
	public void apply (CommitStatus status) throws IOException, GitAPIException {
		
		GitLog gitLog = new GitLog(status.getRepositoryDirectory(), status.getBranchName(), status.getOldCommitSha(), status.getNewCommitSha());
	
		RepositoryBean repository = repositoryRepository.findByOrganizationNameAndRepositoryName(status.getOrganizationName(), status.getRepositoryName());
		Hibernate.initialize(repository.getCommits());
		
		for (CommitInfo commitInfo : gitLog.getCommitInfos()) {
			
			CommitBean commit = new CommitBean(repository, commitInfo.getCommitSha());
			
			List<GitDocument> gitDocuments = new GitFolderStructure(status.getRepositoryDirectory(), commitInfo.getCommitSha()).getGitDocuments();
			for (GitDocument gitDocument : gitDocuments) {

				/*
				 * TODO:
				 * Need to distinguish whether this document is modified or not.
				 */
				DocumentBean document = new DocumentModifiedBean(commit, gitDocument.getRelativeFilepath());
				commit.addDocument(document);
			}
//			showFolderStructure(gitCommit);
//			
			repository.addCommit(commit);
		}
		
		/*
		 * TODO:
		 * GitLog gives all the previous commits related to the current 
		 * branch (so include the one previous share with other branch).
		 * Therefore, it is possible that one commit already exists in
		 * the SQL database system (notice that SQL doesn't in charge of
		 * the part of the topology/relationship of the commits).
		 * 
		 * Need to think carefully the condition that post-receive have 
		 * more then one line of stdin (I don't know any condition until
		 * now) and check whether the above condition is possible. If 
		 * yes, need to write an exceptional condition somewhere in here.
		 */
		repositoryRepository.saveAndFlush(repository);
	}
	
//	private static void showFolderStructure (GitFolderStructure gitCommit) {
//		showHierarchy(gitCommit.getFolderStructure(), 0);
//	}
//	
//	private static void showHierarchy (GitFolderStructure.ListableTreeNode parentNode, int level) {
//		
//		for (int i = 0; i < level; ++i) {
//			System.out.print("\t");
//		}
//		System.out.println(parentNode);
//		
//		for(GitFolderStructure.ListableTreeNode node : parentNode.childrenList()) {
//			showHierarchy(node, level+1);
//		}
//	}
}
