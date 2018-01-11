package enterovirus.capsid.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerMapping;

import enterovirus.enzymark.htmlgenerator.DesignDocumentHtmlGenerator;
import enterovirus.gitar.wrap.BranchName;
import enterovirus.protease.database.*;
import enterovirus.protease.domain.*;

@Controller
public class GitNavigationController {	

	@Autowired private RepositoryRepository repositoryRepository;
	@Autowired private RepositoryCommitLogRepository repositoryCommitLogRepository;
	@Autowired private CommitRepository commitRepository;
	@Autowired private DocumentRepository documentRepository;
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/branches/{branchName}/commits", method=RequestMethod.GET)
	public String showCommitList (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			@PathVariable BranchName branchName,
			Model model) throws Exception {
		
		RepositoryBean repository = repositoryRepository.findById(repositoryId);
		repositoryCommitLogRepository.loadCommitLog(repository, branchName);
		
		model.addAttribute("organization", repository.getOrganization());
		model.addAttribute("repository", repository);
		
		return "git-navigation/commit-list";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}", method=RequestMethod.GET)
	public String showFolderStructure (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			Model model) throws IOException {
		
		CommitBean commit = commitRepository.findByRepositoryId(repositoryId);
		RepositoryBean repository = commit.getRepository();
		model.addAttribute("organization", repository.getOrganization());
		model.addAttribute("repository", repository);
		
		if (commit instanceof CommitValidBean) {
			model.addAttribute("folderStructure", ((CommitValidBean)commit).getFolderStructure());
			return "git-navigation/commit";
		}
		else if (commit instanceof CommitInvalidBean) {
			model.addAttribute("errorMessage", ((CommitInvalidBean)commit).getErrorMessage());
			return "git-navigation/error-commit";
		}
		
		/*
		 * TODO:
		 * Raise exception in this case.
		 */
		return "";
	}
	
	/*
	 * Current only the folder_1/same-name-file works,
	 * because the fake database is not complete.
	 * 
	 * http://localhost:8888/organizations/1/repositories/1/directories/folder_1/same-name-file
	 * 
	 * TODO:
	 * This shouldn't go with DocumentBean, because it need to
	 * display files with are not documents (e.g. images).
	 * Should define another one with URL such as
	 * /organizations/{organizationId}/repositories/{repositoryId}/documents/{documentId}
	 * to handle the display of documents.
	 * 
	 * TODO:
	 * We can't do "documents/{documentId}". Link between traceable items (and
	 * the corresponding documents) need to set up in a relative way.
	 */
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/directories/**", method=RequestMethod.GET)
	public String navigateRepositoryContent (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			HttpServletRequest request,
			Model model) throws IOException {
		
		String wholePath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		String filepath = new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, wholePath);
		
		DocumentBean document = documentRepository.findByRepositoryIdAndRelativeFilepath(repositoryId, filepath);
		model.addAttribute("document", document);
		
		CommitBean commit = document.getCommit();
		RepositoryBean repository = commit.getRepository();
		model.addAttribute("organization", repository.getOrganization());
		model.addAttribute("repository", repository);
		
		DesignDocumentHtmlGenerator contentParser = new DesignDocumentHtmlGenerator(document.getContent(), document.getOriginalDocument());
		model.addAttribute("content", contentParser.getHtml());
		
		return "git-navigation/document";
	}
}