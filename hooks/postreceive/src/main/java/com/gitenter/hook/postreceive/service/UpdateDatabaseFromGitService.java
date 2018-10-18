package com.gitenter.hook.postreceive.service;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitenter.enzymark.propertiesfile.PropertiesFileFormatException;
import com.gitenter.enzymark.propertiesfile.PropertiesFileParser;
import com.gitenter.enzymark.tracefactory.CommitBeanFactory;
import com.gitenter.gitar.GitCommit;
import com.gitenter.protease.dao.auth.RepositoryRepository;
import com.gitenter.protease.dao.git.CommitRepository;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.git.BranchBean;
import com.gitenter.protease.domain.git.CommitBean;
import com.gitenter.protease.domain.git.IgnoredCommitBean;
import com.gitenter.protease.domain.git.InvalidCommitBean;

@Service
public class UpdateDatabaseFromGitService {

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
	private void updateGitCommit(File repositoryDirectory, RepositoryBean repository, GitCommit gitCommit) 
			throws IOException, CheckoutConflictException, GitAPIException {

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
		
		CommitBeanFactory factory = new CommitBeanFactory();
		CommitBean commit = factory.getCommit(gitCommit);
		
		/*
		 * TODO:
		 * This piece of code (until return) is duplicated.
		 */
		commit.setRepository(repository);
		repository.addCommit(commit);
		
		commitRepository.saveAndFlush(commit);
		return;
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
