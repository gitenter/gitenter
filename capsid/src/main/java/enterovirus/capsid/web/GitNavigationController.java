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

import enterovirus.protease.database.*;
import enterovirus.protease.domain.*;

@Controller
public class GitNavigationController {	

	@Autowired private CommitRepository commitRepository;
	@Autowired private DocumentRepository documentRepository;
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}", method=RequestMethod.GET)
	public String showRepository (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			Model model) throws IOException {
		
		CommitBean commit = commitRepository.findByRepositoryId(repositoryId);
		RepositoryBean repository = commit.getRepository();
		model.addAttribute("organization", repository.getOrganization());
		model.addAttribute("repository", repository);
		
		model.addAttribute("folderStructure", commit.getFolderStructure());
		
		return "git-navigation/repository";
	}
	
	/*
	 * Current only the folder_1/same-name-file works,
	 * because the fake database is not complete.
	 * 
	 * http://localhost:8888/organizations/1/repositories/1/directories/folder_1/same-name-file
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
		
		return "git-navigation/document";
	}
}