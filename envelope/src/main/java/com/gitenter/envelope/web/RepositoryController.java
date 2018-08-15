package com.gitenter.envelope.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.gitenter.envelope.service.RepositoryService;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.auth.RepositoryMemberRole;
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
}
