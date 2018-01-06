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
		
		CommitBean commit = new CommitBean(repository, commitInfo.getCommitSha());
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
		 *
		 * NOTE 1:
		 * We can only use: commitRepository.saveAndFlush(commit);
		 * but not: repositoryRepository.saveAndFlush(repository);
		 *   
		 * For the second case, "commit.id" will not be updated,
		 * so it will raise errors when we do "commitRepository.saveAndFlush(commit)"
		 * in later part of this script.
		 * 
		 * NOTE 2:
		 * Data will be saveAndFlush again after the first and the
		 * second round (see later comments). It doesn't really matter
		 * whether commit is saved or not in here. We do it here
		 * as it makes irrelevant things separately handled, but it
		 * doesn't really causes performance overhead (since the number
		 * of commits is really tiny compare to other operations).
		 */
//		repositoryRepository.saveAndFlush(repository);
		commitRepository.saveAndFlush(commit);

		/*
		 * Communicate with git to get the useful information needed
		 * to be written to the database. This part doesn't touch the
		 * persistence layer and/or the database.
		 * 
		 * TODO:
		 * Consider add a config file to indicate that this system only 
		 * traces files of some particular folders/paths.
		 */
		TraceableRepository traceableRepository = getTraceableRepository(status, commitInfo);
		
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
	
	private TraceableRepository getTraceableRepository (CommitStatus status, CommitInfo commitInfo) throws IOException {

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
			CommitBean commit, 
			TraceableRepository traceableRepository) {
		
		TraceabilityBuildHelper helper = new TraceabilityBuildHelper();
		
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
				helper.traceabilityIterateMap.put(traceableItem, itemBean);
				helper.traceablilityBuilderMap.put(itemTag, itemBean);
				
				documentBean.addTraceableItem(itemBean);
			}
			commit.addDocument(documentBean);
		}
		
		/*
		 * NOTE 1:
		 * We need to flush twice, ones after each loop, because otherwise the
		 * document and traceable IDs haven't been update yet. It will cause 
		 * error when Hibernate try to "insert into git.traceability_map" --
		 * The upstream item and document will have "null" id value.
		 * 
		 * NOTE 2:
		 * The code currently in here is really tricky. We do
		 * "documentRepository.saveAndFlush" rather than "commitRepository.saveAndFlush",
		 * because in later case "itemBean.getOriginalDocument()" will
		 * query the database again and give another Java Object (with a 
		 * different object ID) rather than refer to the one already
		 * exists. Therefore, When we saveAndFlush after the second loop,
		 * it doesn't see any referred "TraceabilityMapBean", so nothing
		 * will be updated.
		 * 
		 * TODO: (because of NOTE 2)
		 * The code is working right now, but the current approach is
		 * probably really risky (as it depend on the detail of Hibernate
		 * which is not by design. So we probably want to change it later.
		 */
		for (DocumentBean documentBean : commit.getDocuments()) {
			documentRepository.saveAndFlush(documentBean);
		}
//		commitRepository.saveAndFlush(commit);
		
		return helper;
	}
	
	private void buildTraceabilityMaps (
			CommitBean commit,
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
			
				/*
				 * TODO:
				 * Currently "upstreamDocument" and "downstreamDocument"
				 * are not correct. They are not the DocumentModified of the
				 * original document, but the document belong to the correct 
				 * commit.
				 * Need to correct it together when we modify to let
				 * "DocumentUnmodifiedBean" involve.
				 */
				TraceableItemBean downstreamItemBean = itemBean;
				DocumentBean downstreamDocument = itemBean.getOriginalDocument();
				TraceableItemBean upstreamItemBean = helper.traceablilityBuilderMap.get(upstreamItem.getTag());
				DocumentBean upstreamDocument = upstreamItemBean.getOriginalDocument();
				
				TraceabilityMapBean map = new TraceabilityMapBean(upstreamDocument, upstreamItemBean, downstreamDocument, downstreamItemBean);
				
				downstreamItemBean.addUpstreamMap(map);
				upstreamItemBean.addDownstreamMap(map);
				downstreamDocument.addMapForADownstreamItem(map);
				upstreamDocument.addMapForAUpstreamItem(map);
			}
		}
		
		/*
		 * NOTE:
		 * In here we "commitRepository.saveAndFlush", rather than
		 * "documentRepository.saveAndFlush", because in later case
		 * every "TraceabilityMapBean" will be saved twice (since
		 * it is referred by two "DocumentBean"s hence causes key
		 * constrain crashes.
		 */
		commitRepository.saveAndFlush(commit);
//		for (DocumentBean documentBean : commit.getDocuments()) {
//			documentRepository.saveAndFlush(documentBean);
//		}
	}
}
