package com.gitenter.protease.dao.git;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gitenter.protease.domain.git.CommitBean;
import com.gitenter.protease.domain.git.DocumentBean;
import com.gitenter.protease.domain.git.IgnoredCommitBean;
import com.gitenter.protease.domain.git.InvalidCommitBean;
import com.gitenter.protease.domain.git.ValidCommitBean;
import com.gitenter.protease.domain.traceability.TraceableItemBean;

@Repository
public class CommitRepositoryImpl implements CommitRepository {

	@Autowired private CommitDatabaseRepository commitDatabaseRepository;
	@Autowired private CommitGitUpdateFactory commitGitUpdateFactory;
	
	public Optional<CommitBean> findById(Integer id) throws IOException, GitAPIException {
		
		Optional<CommitBean> items = commitDatabaseRepository.findById(id);
		
		if (items.isPresent()) {
			CommitBean item = items.get();
			commitGitUpdateFactory.update(item);
		}
		
		return items;
	}
	
	public List<CommitBean> findByRepositoryIdAndCommitSha(Integer repositoryId, String commitSha) throws IOException, GitAPIException {
		
		List<CommitBean> items = commitDatabaseRepository.findByRepositoryIdAndSha(repositoryId, commitSha);
		
		for (CommitBean item : items) {
			commitGitUpdateFactory.update(item);
		}
		
		return items;
	}
	
	public List<CommitBean> findByRepositoryIdAndCommitShaIn(Integer repositoryId, List<String> commitShas) throws IOException, GitAPIException {
		
		List<CommitBean> items = commitDatabaseRepository.findByRepositoryIdAndShaIn(repositoryId, commitShas);
		
		/*
		 * TODO:
		 * This approach is not good, as it will query from git multiple times.
		 * think about a way that it can be done in one single git query.
		 */
		for (CommitBean item : items) {
			commitGitUpdateFactory.update(item);
		}
		
		return items;
	}
	
	public void deleteById(Integer id) {
		commitDatabaseRepository.deleteById(id);
	}
	
	public CommitBean saveAndFlush(CommitBean commit) {
		
		if (commit instanceof InvalidCommitBean) {
			return commitDatabaseRepository.saveAndFlush(commit);
		}
		
		if (commit instanceof IgnoredCommitBean) {
			return commitDatabaseRepository.saveAndFlush(commit);
		}
		
		/*
		 * This workaround is for cases when there are multiple documents
		 * which traceable items linked in between.
		 * If not, Hibernate will confuse itself by complaining:
		 * 
		 * >  org.postgresql.util.PSQLException: ERROR: null value in column 
		 * > "document_id" violates not-null constraint
		 * > Detail: Failing row contains (21, null, tag-2, content-2).
		 * 
		 * See failed unit tests if we remove this workaround.
		 */
		assert commit instanceof ValidCommitBean;
		ValidCommitBean validCommit = (ValidCommitBean)commit;
		
		Map<DocumentBean,List<TraceableItemBean>> traceableItemCache = new HashMap<DocumentBean,List<TraceableItemBean>>();
		for (DocumentBean document : validCommit.getDocuments()) {
			traceableItemCache.put(document, document.getTraceableItems());
			document.setTraceableItems(null);
		}
		commitDatabaseRepository.saveAndFlush(commit);
		
		for (DocumentBean document : validCommit.getDocuments()) {
			document.setTraceableItems(traceableItemCache.get(document));
		}
		return commitDatabaseRepository.saveAndFlush(commit);
	}
}
