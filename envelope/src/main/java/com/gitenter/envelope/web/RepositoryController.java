package com.gitenter.envelope.web;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.gitenter.envelope.service.RepositoryService;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.auth.RepositoryMemberRole;
import com.gitenter.protease.domain.git.BranchBean;
import com.gitenter.protease.domain.git.CommitBean;
import com.gitenter.protease.domain.git.IgnoredCommitBean;
import com.gitenter.protease.domain.git.InvalidCommitBean;
import com.gitenter.protease.domain.git.ValidCommitBean;
import com.gitenter.protease.source.WebSource;

@Controller
public class RepositoryController {
	
	@Autowired WebSource webSource;
	
	@Autowired RepositoryService repositoryService;

	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}", method=RequestMethod.GET)
	public String showCommitDefault (
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
	public String showCommitByBranch (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			@PathVariable String branchName,
			HttpServletRequest request,
			Model model) throws Exception {
		
		model.addAttribute("branch", branchName);
		
		/*
		 * TODO:
		 * Default show not the most recent commit, rather than the most recent
		 * valid commit in this branch.
		 */
		CommitBean commit = repositoryService.getCommitFromBranchName(repositoryId, branchName);
		return showCommit(commit, request, model);
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/commits/{commitSha}", method=RequestMethod.GET)
	public String showCommitByCommit (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			@PathVariable String commitSha,
			HttpServletRequest request,
			Model model) throws Exception {
		
		model.addAttribute("commitSha", commitSha);
		
		CommitBean commit = repositoryService.getCommitFromSha(repositoryId, commitSha);
		return showCommit(commit, request, model);
	}
	
	private String showCommit (
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
		
		model.addAttribute("branchNames", repository.getBranchNames());
		model.addAttribute("repositoryMemberRoleValues", RepositoryMemberRole.values());
		
		if (commit instanceof ValidCommitBean) {
			
			ValidCommitBean validCommit = (ValidCommitBean)commit;
			System.out.println(validCommit);
			System.out.println(validCommit.getRoot());
			
			model.addAttribute("root", validCommit.getRoot());
			
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
}
