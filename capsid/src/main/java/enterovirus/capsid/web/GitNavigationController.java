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

import enterovirus.capsid.database.*;
import enterovirus.capsid.domain.*;

@Controller
public class GitNavigationController {	

	@Autowired private CommitRepository commitRepository;
	
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
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/directories/**", method=RequestMethod.GET)
	public String navigateRepositoryContent (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			HttpServletRequest request
			) {
		
		String wholePath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
	    String filepath = new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, wholePath);
		
		System.out.println(organizationId);
		System.out.println(repositoryId);
		System.out.println(filepath);
		
		return "git-navigation/document";
	}
}