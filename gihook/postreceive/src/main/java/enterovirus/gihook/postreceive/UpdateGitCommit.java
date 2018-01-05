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
		System.out.println("===");
		for (CommitBean commit : repository.getCommits()) {
			System.out.println(commit.getShaChecksumHash());
		}
		System.out.println("===");
		
		for (CommitInfo commitInfo : gitLog.getCommitInfos()) {
			
			CommitBean commit = new CommitBean(repository, commitInfo.getCommitSha());
			System.out.println(commit.getShaChecksumHash());
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
			repositoryRepository.saveAndFlush(repository);
			System.out.println("id: "+commit.getId());
//			commitRepository.saveAndFlush(commit);
			
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
			
			Map<TraceableItem,TraceableItemBean> traceabilityIterateMap = new HashMap<TraceableItem,TraceableItemBean>();
			Map<String,TraceableItemBean> traceablilityBuilderMap = new HashMap<String,TraceableItemBean>();
			
			/*
			 * First round to build all traceable items.
			 */
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
					traceabilityIterateMap.put(traceableItem, itemBean);
					traceablilityBuilderMap.put(itemTag, itemBean);
					
					documentBean.addTraceableItem(itemBean);
				}
				commit.addDocument(documentBean);
			}
			
			/*
			 * Need to flush "commit" two times, because otherwise the
			 * document and traceable IDs haven't been update yet.
			 * 
			 * TODO:
			 * Currently it doesn't work. It add try to add another commit
			 * rather than update the one in the previous 
			 * "repositoryRepository.saveAndFlush(repository)"
			 * Gives error:
			 *  
			 * duplicate key value violates unique constraint "git_commit_sha_checksum_hash_key"
			 * Detail: Key (sha_checksum_hash)=(425f5ad854ec1220bb5373d76a42d1980971d11b) already exists.
			 */
			commitRepository.saveAndFlush(commit);
			
			/*
			 * TODO:
			 * Second round to retrieve traceability map.
			 */
			for (Map.Entry<TraceableItem,TraceableItemBean> entry : traceabilityIterateMap.entrySet()) {
				
				TraceableItem traceableItem = entry.getKey();
				TraceableItemBean itemBean = entry.getValue();
				
				System.out.println("traceableItem: "+traceableItem.getTag());
				System.out.println("itemBean: "+itemBean.getItemTag());
				
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
					TraceableItemBean upstreamItemBean = traceablilityBuilderMap.get(upstreamItem.getTag());
					DocumentBean upstreamDocument = upstreamItemBean.getOriginalDocument();
					
					System.out.println("--upstreamItemBean: "+upstreamItemBean.getItemTag());
//					System.out.println("--downstreamItemBean: "+downstreamItemBean.getItemTag());
					
					TraceabilityMapBean map = new TraceabilityMapBean(upstreamDocument, upstreamItemBean, downstreamDocument, downstreamItemBean);
					downstreamItemBean.addUpstreamMap(map);
					upstreamItemBean.addDownstreamMap(map);
				}
			}
			
//			commitRepository.saveAndFlush(commit);
		}
	}
}
