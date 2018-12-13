package com.gitenter.capsid.web;

import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.HandlerMapping;

import com.gitenter.capsid.service.RepositoryService;
import com.gitenter.enzymark.htmlgenerator.DesignDocumentHtmlGenerator;
import com.gitenter.enzymark.htmlgenerator.HtmlGenerator;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.auth.RepositoryMemberRole;
import com.gitenter.protease.domain.git.BranchBean;
import com.gitenter.protease.domain.git.CommitBean;
import com.gitenter.protease.domain.git.DocumentBean;
import com.gitenter.protease.domain.git.FileBean;
import com.gitenter.protease.domain.git.IgnoredCommitBean;
import com.gitenter.protease.domain.git.InvalidCommitBean;
import com.gitenter.protease.domain.git.ValidCommitBean;
import com.gitenter.protease.source.WebSource;

@Controller
public class RepositoryController {
	
	@Autowired WebSource webSource;
	
	@Autowired RepositoryService repositoryService;

	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}", method=RequestMethod.GET)
	public String showRepository (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			@ModelAttribute("branch") String branch,
			Model model) throws Exception {
		
		RepositoryBean repository = repositoryService.getRepository(repositoryId);
		
		/*
		 * The repository has not been initialized yet.
		 * The corresponding folder is empty.
		 */
		if (repository.getCommitCount() == 0) {
		
			model.addAttribute("rootUrl", webSource.getDomainName());
			
			model.addAttribute("organization", repository.getOrganization());
			model.addAttribute("repository", repository);
			
			model.addAttribute("repositoryMemberRoleValues", RepositoryMemberRole.values());
			
			return "repository/setup-a-new-repository";
		}
		
		/*
		 * TODO:
		 * 
		 * Query database to get customized default branch name for each single repository.
		 */
		String branchName = "master";
		return "redirect:/organizations/"+organizationId+"/repositories/"+repositoryId+"/branches/"+branchName;
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/branches/{branchName}", method=RequestMethod.GET)
	public String showBranchHead (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			@PathVariable String branchName,
			HttpServletRequest request,
			Model model) throws Exception {
		
		model.addAttribute("branchName", branchName);
		
		/*
		 * TODO:
		 * Default show not the most recent commit, rather than the most recent
		 * valid commit in this branch.
		 */
		CommitBean commit = repositoryService.getCommitFromBranchName(repositoryId, branchName);
		return showCommitDetail(commit, request, model);
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/commits/{commitSha}", method=RequestMethod.GET)
	public String showCommit (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			@PathVariable String commitSha,
			HttpServletRequest request,
			Model model) throws Exception {
		
		model.addAttribute("commitSha", commitSha);
		
		CommitBean commit = repositoryService.getCommitFromSha(repositoryId, commitSha);
		return showCommitDetail(commit, request, model);
	}
	
	private String showCommitDetail (
			CommitBean commit, 
			HttpServletRequest request, 
			Model model) throws Exception {

		model.addAttribute("rootUrl", webSource.getDomainName());
		
		String currentUrl = request.getRequestURL().toString();
		model.addAttribute("currentUrl", currentUrl);
		
		RepositoryBean repository = commit.getRepository();
		OrganizationBean organization = repository.getOrganization();
		Hibernate.initialize(repository.getRepositoryMemberMaps());
		
		model.addAttribute("organization", organization);
		model.addAttribute("repository", repository);
		model.addAttribute("commit", commit);
		
		model.addAttribute("branchNames", repository.getBranchNames());
		model.addAttribute("repositoryMemberRoleValues", RepositoryMemberRole.values());
		
		if (commit instanceof ValidCommitBean) {
			model.addAttribute("root", ((ValidCommitBean)commit).getRoot());			
			return "repository/valid-commit";
		}
		else if (commit instanceof InvalidCommitBean) {
			/*
			 * TODO:
			 * Can it show all the parsing exceptions at the same time?
			 * Or a better way is to have a client-side hook to handle that?
			 */
			model.addAttribute("errorMessage", ((InvalidCommitBean)commit).getErrorMessage());
			return "repository/invalid-commit";
		}
		else if (commit instanceof IgnoredCommitBean) {
			return "repository/ignored-commit";
		}
		
		/*
		 * TODO:
		 * Raise exception in this case.
		 */
		return "";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/branches/{branchName}/commits", method=RequestMethod.GET)
	public String showCommitList (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			@PathVariable String branchName,
			@RequestParam(value="page", required=false, defaultValue="0") Integer page,
			HttpServletRequest request,
			Model model) throws Exception {
		
		final Integer itemPerPage = 10;
		
		RepositoryBean repository = repositoryService.getRepository(repositoryId);
		model.addAttribute("repository", repository);
		model.addAttribute("organization", repository.getOrganization());
		
		model.addAttribute("branchName", branchName);
		model.addAttribute("page", page);
		
		Collection<BranchBean> branches = repository.getBranches();
		model.addAttribute("branches", branches);
		
		BranchBean branch = repository.getBranch(branchName);
		List<CommitBean> commits = branch.getInDatabaseLog(itemPerPage, itemPerPage*page);
		model.addAttribute("commits", commits);

		/* 
		 * TODO:
		 * Connect the git user (name and email) to the user of
		 * this website.
		 */
		return "repository/commit-list";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/documents/directories/**", method=RequestMethod.GET)
	public String showDocumentContentDefault (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			@ModelAttribute("branch") String branch,
			HttpServletRequest request) {
		
		/*
		 * TODO:
		 * 
		 * Query database to get customized default branch name for each single repository.
		 */
		String branchName = "master";
		String filepath = getWildcardValue(request);
		return "redirect:/organizations/"+organizationId+"/repositories/"+repositoryId+"/branches/"+branchName+"/documents/directories/"+filepath;
	}
	
	/*
	 * This functions go with URL "/directories/**".
	 * 
	 * They cannot go with "/documents/{documentId}", because link 
	 * between traceable items (and the corresponding documents) 
	 * need to set up in a relative way.
	 * 
	 * TODO:
	 * Need to handle blobs (e.g. images) which are not documents.
	 * (1) They are not written into document table.
	 * (2) They need to be parsed and shown in a different way.
	 */
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/commits/{commitSha}/documents/directories/**", method=RequestMethod.GET)
	public String showDocumentContentByCommit (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			@PathVariable String commitSha,
			HttpServletRequest request,
			Model model) throws Exception {
		
		model.addAttribute("commitSha", commitSha);
		
		String relativePath = getWildcardValue(request);
		model.addAttribute("relativePath", relativePath);
		DocumentBean document = repositoryService.getDocumentFromCommitShaAndRelativePath(commitSha, relativePath);
		
		return showDocumentContent(document, request, model);
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/branches/{branchName}/documents/directories/**", method=RequestMethod.GET)
	public String showDocumentContentByBranch (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			@PathVariable String branchName,
			HttpServletRequest request,
			Model model) throws Exception {
		
		model.addAttribute("branch", branchName);
		
		String relativePath = getWildcardValue(request);
		model.addAttribute("relativePath", relativePath);
		DocumentBean document = repositoryService.getDocumentFromRepositoryIdAndBranchAndRelativePath(repositoryId, branchName, relativePath);
		
		return showDocumentContent(document, request, model);
	}
	
	private String showDocumentContent (
			DocumentBean document, 
			HttpServletRequest request,
			Model model) throws Exception {
		
		CommitBean commit = document.getCommit();
		RepositoryBean repository = commit.getRepository();
		OrganizationBean organization = repository.getOrganization();
		
		model.addAttribute("organization", organization);
		model.addAttribute("repository", repository);
		model.addAttribute("commit", commit);
		model.addAttribute("document", document);
		
		model.addAttribute("branchNames", repository.getBranchNames());
		
		HtmlGenerator htmlGenerator = new DesignDocumentHtmlGenerator(document);
		model.addAttribute("content", htmlGenerator.getHtml());
		
		return "repository/document";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/commits/{commitSha}/blobs/directories/**", method=RequestMethod.GET)
	public void showBlobContentByCommit (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			@PathVariable String commitSha,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String relativePath = getWildcardValue(request);
		FileBean file = repositoryService.getFileFromRepositoryIdAndCommitShaAndRelativePath(repositoryId, commitSha, relativePath);
		writeToOutputStream(file, response);
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/branches/{branchName}/blobs/directories/**", method=RequestMethod.GET)
	public void showBlobContentByBranch (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			@PathVariable String branchName,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String relativePath = getWildcardValue(request);
		FileBean file = repositoryService.getFileFromRepositoryIdAndBranchAndRelativePath(repositoryId, branchName, relativePath);
		writeToOutputStream(file, response);
	}
	
	private void writeToOutputStream (FileBean file, HttpServletResponse response) throws Exception {
		
		response.setContentType(file.getMimeType());
		OutputStream outputStream = response.getOutputStream();
		outputStream.write(file.getBlobContent());
		outputStream.close();
	}
	
	private String getWildcardValue (HttpServletRequest request) {
		
		String wholePath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		String wildcard = new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, wholePath);
		
		return wildcard;
	}
}
