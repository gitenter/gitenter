package enterovirus.gihook.postreceive;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import enterovirus.gihook.postreceive.status.CommitStatus;
import enterovirus.gihook.postreceive.traceanalyzer.*;
import enterovirus.gitar.GitBlob;
import enterovirus.gitar.GitFolderStructure;
import enterovirus.gitar.GitLog;
import enterovirus.gitar.wrap.CommitInfo;
import enterovirus.protease.database.*;
import enterovirus.protease.domain.*;

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
			
			List<GitBlob> blobs = new GitFolderStructure(status.getRepositoryDirectory(), commitInfo.getCommitSha()).getGitBlobs();
			
			TraceableRepository traceableRepository = new TraceableRepository(status.getRepositoryDirectory());
			for (GitBlob blob : blobs) {
				
				/*
				 * TODO:
				 * Need to distinguish whether this document is modified or not.
				 */
				File filepath = new File(status.getRepositoryDirectory(), blob.getRelativeFilepath());
				String relativeFilepath = blob.getRelativeFilepath();
				String textContent = new String(blob.getBlobContent());
				TraceableDocument traceableDocument = new TraceableDocument(traceableRepository, relativeFilepath, textContent);
				traceableRepository.addTraceableDocument(traceableDocument);
			}
			traceableRepository.refreshUpstreamAndDownstreamItems();
			
			Map<String,TraceableItemBean> traceablilityBuilderMap = new HashMap<String,TraceableItemBean>();
			for (TraceableDocument traceableDocument : traceableRepository.getTraceableDocuments()) {
				
				/*
				 * TODO:
				 * If this document if from a previous commit, then in here we should
				 * not do this, but load the corresponding "DocumentModifiedBean", and 
				 * create an new "DocumentUnmodifiedBean".
				 */
				DocumentModifiedBean documentBean = new DocumentModifiedBean(commit, traceableDocument.getRelativeFilepath());
				
				for (TraceableItem traceableItem : traceableDocument.getTraceableItems()) {
					
					Integer lineNumber = traceableItem.getLineNumber();
					String itemTag = traceableItem.getTag();
					String content = traceableItem.getContent();
					
					TraceableItemBean itemBean = new TraceableItemBean(documentBean, lineNumber, itemTag, content);
					traceablilityBuilderMap.put(itemTag, itemBean);
					
					documentBean.addTraceableItem(itemBean);
				}
				
				/*
				 * TODO:
				 * Another loop to retrieve traceability map.
				 */
				
				commit.addDocument(documentBean);
			}
			
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
}
