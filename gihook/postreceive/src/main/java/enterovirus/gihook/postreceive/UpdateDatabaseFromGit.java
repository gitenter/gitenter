package enterovirus.gihook.postreceive;

import java.util.ArrayList;
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

import com.gitenter.protease.dao.auth.RepositoryRepository;
import com.gitenter.protease.dao.git.CommitRepository;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.git.BranchBean;
import com.gitenter.protease.domain.git.CommitBean;
import com.gitenter.protease.domain.git.IgnoredCommitBean;
import com.gitenter.protease.domain.git.InvalidCommitBean;
import com.gitenter.protease.domain.git.ValidCommitBean;
import com.gitenter.enzymark.propertiesfile.PropertiesFileFormatException;
import com.gitenter.enzymark.propertiesfile.PropertiesFileParser;
import com.gitenter.enzymark.traceanalyzer.*;
import com.gitenter.gitar.GitCommit;

@Service
public class UpdateDatabaseFromGit {

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
	public void update (HookInputSet input) throws IOException, GitAPIException {
		
		RepositoryBean repository = repositoryRepository.findByOrganizationNameAndRepositoryName(
				input.getOrganizationName(), input.getRepositoryName()).get(0);
		BranchBean branch = repository.getBranch(input.getBranchName());
		
		/*
		 * Ideally for commits in between the two provided SHAs, they should all
		 * be unsaved. However, `branch.getUnsavedLog()` actually queries the database
		 * and remove all the existing ones.
		 * 
		 * We need to do it, because for a new branch, although it actually starts from 
		 * an existing commit, the `oldSha` "update"/"post-receive" hooks provided
		 * is still a null value. So if we don't manually find these commit out,
		 * they'll be try to rewrite to the database again, and that raises SQL
		 * error 
		 * > ERROR: duplicate key value violates unique constraint
		 * 
		 * This also makes the hook idempotent, which is more robust for further maintenance.
		 */
		for (GitCommit gitCommit : branch.getUnsavedLog(input.getOldSha(), input.getNewSha())) {
			
			/*
			 * Update every single git commit which is under the
			 * new "git push". 
			 */
			updateGitCommit(input.getRepositoryDirectory(), repository, gitCommit);
		}
	}
	
	/*
	 * TODO:
	 * 
	 * User may want to redo the pushed commit
	 * (e.g. https://stackoverflow.com/questions/22682870/git-undo-pushed-commits),
	 * then the database should be cleaned up.
	 */
	private void updateGitCommit(File repositoryDirectory, RepositoryBean repository, GitCommit gitCommit) throws IOException {

		PropertiesFileParser propertiesFileParser;
		try {
			propertiesFileParser = new PropertiesFileParser(repositoryDirectory, gitCommit.getSha(), "gitenter.properties"); 
		}
		catch (PropertiesFileFormatException e) {

			InvalidCommitBean commit = new InvalidCommitBean();
			commit.setErrorMessage(e.getMessage());
			
			/*
			 * TODO:
			 * This piece of code (until return) is duplicated.
			 */
			commit.setRepository(repository);
			commit.setFromGitCommit(gitCommit);
			repository.addCommit(commit);
			
			commitRepository.saveAndFlush(commit);
			return;
		}
		
		if (propertiesFileParser.isEnabledSystemwide() == false) {
			
			IgnoredCommitBean commit = new IgnoredCommitBean();
			
			commit.setRepository(repository);
			commit.setFromGitCommit(gitCommit);
			repository.addCommit(commit);
			
			commitRepository.saveAndFlush(commit);
			return;
		}
			
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
			String[] includePaths = propertiesFileParser.getIncludePaths();
			traceableRepository = getTraceableRepository(status, gitCommit, includePaths);
		}
		catch (TraceAnalyzerException e) {
			
			InvalidCommitBean commit = new InvalidCommitBean();
			
			/*
			 * TODO:
			 * Can it show all the parsing exceptions at the same time (the current
			 * approach can only show the first exception which errors out)?
			 * Or a better way is to have a client-side hook to handle that?
			 * 
			 * Probably need to recover from the "TraceAnalyzerException"
			 * and continue append the error messages. 
			 */
			commit.setErrorMessage(e.getMessage());
			
			/*
			 * TODO:
			 * This piece of code (until return) is duplicated.
			 */
			commit.setRepository(repository);
			commit.setFromGitCommit(gitCommit);
			repository.addCommit(commit);
			
			commitRepository.saveAndFlush(commit);
			return;
		}
			
		ValidCommitBean commit = new ValidCommitBean();
		
		commit.setRepository(repository);
		commit.setFromGitCommit(gitCommit);
		repository.addCommit(commit);
		
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
	
	private TraceableRepository getTraceableRepository (HookInputSet status, CommitInfo commitInfo, String[] includePaths) throws IOException, TraceAnalyzerException {

		List<GitBlob> blobs = new GitFolderStructure(status.getRepositoryDirectory(), commitInfo.getCommitSha(), includePaths).getGitBlobs();
		
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
	 * 
	 * Note: Non-static inner class only used for one particular 
	 * instance of the outer one.
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
			 * 
			 * TODO:
			 * May try to build some "backed by" collection (through 
			 * "Collection.retainAll()") so the collections of 
			 * "TraceableItem" and "TraceableItemBean" can be handled
			 * together.
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
