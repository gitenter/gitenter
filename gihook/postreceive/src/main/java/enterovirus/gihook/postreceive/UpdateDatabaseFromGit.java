package enterovirus.gihook.postreceive;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import enterovirus.enzymark.traceanalyzer.*;
import enterovirus.gihook.postreceive.status.CommitStatus;
import enterovirus.gitar.GitBlob;
import enterovirus.gitar.GitFolderStructure;
import enterovirus.gitar.GitLog;
import enterovirus.gitar.wrap.CommitInfo;
import enterovirus.protease.database.*;
import enterovirus.protease.domain.*;

@Service
public class UpdateDatabaseFromGit {

	@Autowired private RepositoryRepository repositoryRepository;
	@Autowired private CommitRepository commitRepository;
	@Autowired private DocumentRepository documentRepository;
	
	/*
	 * TODO:
	 * Cannot do "private". Otherwise cannot initialize lazy evaluation of "commits".
	 * Don't understand why.
	 * 
	 * TODO:
	 * Move the relevant functions to some other classes, such as some controllers. 
	 */
	@Transactional
	public void update (CommitStatus status) throws IOException, GitAPIException {
		
		GitLog gitLog = new GitLog(status.getRepositoryDirectory(), status.getBranchName(), status.getOldCommitSha(), status.getNewCommitSha());
	
		RepositoryBean repository = repositoryRepository.findByOrganizationNameAndRepositoryName(status.getOrganizationName(), status.getRepositoryName());
		Hibernate.initialize(repository.getCommits());
		
		for (CommitInfo commitInfo : gitLog.getCommitInfos()) {
			
			/*
			 * Update every single git commit which is under the
			 * new "git push". 
			 */
			updateGitCommit(status, repository, commitInfo);
		}
	}
	
	private void updateGitCommit (CommitStatus status, RepositoryBean repository, CommitInfo commitInfo) throws IOException {
		
		/* 
		 * TODO:
		 * Consider add a config file to indicate that this system only 
		 * traces files of some particular folders/paths.
		 */
		
		/*
		 * Communicate with git to get the useful information needed
		 * to be written to the database. This part doesn't touch the
		 * persistence layer and/or the database.
		 * 
		 * If it raises exceptions, insert into the database with a
		 * by filling the invalid commit table.
		 */
		TraceableRepository traceableRepository;
		
		try {
			traceableRepository = getTraceableRepository(status, commitInfo);
		}
		catch (TraceAnalyzerException e) {
			
			CommitBean commit = new CommitInvalidBean(repository, commitInfo.getCommitSha(), e.getMessage());
			repository.addCommit(commit);
			
			commitRepository.saveAndFlush(commit);
			return;
		}
			
		CommitValidBean commit = new CommitValidBean(repository, commitInfo.getCommitSha());
		repository.addCommit(commit);
			
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
//		commitRepository.saveAndFlush(commit);
		
		/*
		 * Write the data into the database through the persistence layer.
		 * 
		 * First round to build all traceable items, and the second round
		 * to retrieve the traceability map.
		 * 
		 * Save to database may or may not be included in the following 
		 * methods. In the currently implementation data are saved multiple
		 * times (by the limitation of Hibernate). See detailed comments
		 * inside of the methods.
		 */
		TraceabilityBuildHelper helper = buildDocumentsAndTraceableItems(commit, traceableRepository);
		buildTraceabilityMaps(commit, helper);
	}
	
	private TraceableRepository getTraceableRepository (CommitStatus status, CommitInfo commitInfo) throws IOException, TraceAnalyzerException {

		List<GitBlob> blobs = new GitFolderStructure(status.getRepositoryDirectory(), commitInfo.getCommitSha()).getGitBlobs();
		
		TraceableRepository traceableRepository = new TraceableRepository(status.getRepositoryDirectory());
		for (GitBlob blob : blobs) {

			String relativeFilepath = blob.getRelativeFilepath();

			/*
			 * Only parse markdown files.
			 */
			if (blob.getMimeType().equals("text/markdown")) {
				String textContent = new String(blob.getBlobContent());
				TraceableDocument traceableDocument = new TraceableDocument(traceableRepository, relativeFilepath, textContent);
				traceableRepository.addTraceableDocument(traceableDocument);
			}
		}
		/*
		 * TODO:
		 * Seems this refresh is useless, as we loop this again while copying
		 * the many-to-many relationship. See comment later.
		 */
		traceableRepository.refreshUpstreamAndDownstreamItems();
		
		return traceableRepository;
	}
	
	/*
	 * This class is to link the results of methods
	 * "buildDocumentsAndTraceableItems()" and "buildTraceabilityMaps()"
	 */
	private class TraceabilityBuildHelper {
		private Map<TraceableItem,TraceableItemBean> traceabilityIterateMap = new HashMap<TraceableItem,TraceableItemBean>();
		private Map<String,TraceableItemBean> traceablilityBuilderMap = new HashMap<String,TraceableItemBean>();
	}
	
	private TraceabilityBuildHelper buildDocumentsAndTraceableItems (
			CommitValidBean commit, 
			TraceableRepository traceableRepository) {
		
		TraceabilityBuildHelper helper = new TraceabilityBuildHelper();
		
		for (TraceableDocument traceableDocument : traceableRepository.getTraceableDocuments()) {
			
			DocumentBean documentBean = new DocumentBean(commit, traceableDocument.getRelativeFilepath());
			
			for (TraceableItem traceableItem : traceableDocument.getTraceableItems()) {
				
				String itemTag = traceableItem.getTag();
				String content = traceableItem.getContent();
				
				TraceableItemBean itemBean = new TraceableItemBean(documentBean, itemTag, content);
				helper.traceabilityIterateMap.put(traceableItem, itemBean);
				helper.traceablilityBuilderMap.put(itemTag, itemBean);
				
				documentBean.addTraceableItem(itemBean);
			}
			commit.addDocument(documentBean);
		}
		
		commitRepository.saveAndFlush(commit);
		
		return helper;
	}
	
	private void buildTraceabilityMaps (
			CommitValidBean commit,
			TraceabilityBuildHelper helper) {

		for (Map.Entry<TraceableItem,TraceableItemBean> entry : helper.traceabilityIterateMap.entrySet()) {
			
			TraceableItem traceableItem = entry.getKey();
			TraceableItemBean itemBean = entry.getValue();
			
			/*
			 * TODO:
			 * So here is another iteration, which only use the upstream
			 * information of the many-to-many TraceableItem relations.
			 * Hence it makes
			 * TraceableRepository.refreshUpstreamAndDownstreamItems()
			 * useless.
			 * Consider delete that one, or rewrite this one smarter? 
			 */
			for (TraceableItem upstreamItem : traceableItem.getUpstreamItems()) {
			
				TraceableItemBean downstreamItemBean = itemBean;
				TraceableItemBean upstreamItemBean = helper.traceablilityBuilderMap.get(upstreamItem.getTag());
				
				downstreamItemBean.addUpstreamItem(upstreamItemBean);
				upstreamItemBean.addDownstreamItem(downstreamItemBean);
			}
		}
		
		System.out.println("===========================");
		commitRepository.saveAndFlush(commit);
	}
	
	/*
	 * TODO:
	 * 
	 * In this class, I have several questions regarding "saveAndFlush()".
	 * It can be done in several different places.
	 * (1) When the CommitBean is initialized.
	 * (2) When we have build all traceable items, but no trace relations has
	 * involved.
	 * (3) when the trace relations has been build.
	 * 
	 * For whether to save/not save in this places, or whether the saving 
	 * procedure is by repository/commit/document, several errors raises
	 * in different combinations.
	 * (a) "null" value for "document_id", "traceable_item_id", ...
	 * (b) Double save of some item because of the later @ManyToMany relations.
	 * 
	 * The current code works for what I have tested, but I don't know whether
	 * it is working in general, and/or whether there's a neater way to write it. 
	 */
}
