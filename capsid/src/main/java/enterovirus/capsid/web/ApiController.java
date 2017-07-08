package enterovirus.capsid.web;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.util.AntPathMatcher;

import enterovirus.capsid.database.TextFileRepository;
import enterovirus.capsid.domain.TextFileBean;

@RestController
@RequestMapping("/api")
public class ApiController {

	private TextFileRepository textFileRepository;
	
	@Autowired
	public ApiController(TextFileRepository textFileRepository) {
		this.textFileRepository = textFileRepository;
	}	
	
	/**
	 * List user repositories
	 * <p>
	 * List public repositories the specified user is involved.
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping(value="/users/{username}/repositories", method=RequestMethod.GET)
	public TextFileBean listUserRepositories(
			@PathVariable String username) {
		return null;
	}
	
	/**
	 * List organization repositories
	 * <p>
	 * List public repositories for the specified organization.
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping(value="/organizations/{organization}/repositories", method=RequestMethod.GET)
	public TextFileBean listOrganizationRepositories(
			@PathVariable String orginization) {
		return null;
	}
	
	/**
	 * Get repository information
	 * <p>
	 * Since design control is most for big enterprise projects,
	 * and needs discussion between multiple people, a repository
	 * is always belong to an organization.
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping(value="/organizations/{organization}/repositories/{repositoryName}", method=RequestMethod.GET)
	public TextFileBean getRepositoryInformation(
			@PathVariable String orginization,
			@PathVariable String repositoryName) {
		return null;
	}

	/**
	 * Get directory information (in branch)
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping(value="/organizations/{organization}/repositories/{repositoryName}/branches/{branchName}/directories/**", method=RequestMethod.GET)
	public TextFileBean getDirectoryInformationInBranch(
			@PathVariable String organization,
			@PathVariable String repositoryName,
			@PathVariable String branchName,
			HttpServletRequest request) throws Exception {
		
		String wholePath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
	    String directoryPath = new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, wholePath);
		
		return null;
	}
	
	/**
	 * Get directory information (in commit)
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping(value="/organizations/{organization}/repositories/{repositoryName}/commits/{commitId}/directories/**", method=RequestMethod.GET)
	public TextFileBean getDirectoryInformationInCommit(
			@PathVariable String organization,
			@PathVariable String repositoryName,
			@PathVariable String commitId,
			HttpServletRequest request) throws Exception {
		
		String wholePath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
	    String directoryPath = new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, wholePath);
		
		return null;
	}
	
	/**
	 * Get text file content (in branch)
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping(value="/organizations/{organization}/repositories/{repositoryName}/branches/{branchName}/files/**", method=RequestMethod.GET)
	public TextFileBean getTextFileContentInBranch(
			@PathVariable String organization,
			@PathVariable String repositoryName,
			@PathVariable String branchName,
			HttpServletRequest request) throws Exception {
		
		String wholePath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
	    String filePath = new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, wholePath);
		
		TextFileBean textFile = textFileRepository.findTextFile(organization, repositoryName, branchName, filePath);
		return textFile;
	}
	
	/**
	 * Get text file content (in commit)
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping(value="/organizations/{organization}/repositories/{repositoryName}/commits/{commitId}/files/**", method=RequestMethod.GET)
	public TextFileBean getTextFileContentInCommit(
			@PathVariable String organization,
			@PathVariable String repositoryName,
			@PathVariable String commitId,
			HttpServletRequest request) throws Exception {
		
		String wholePath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
	    String filePath = new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, wholePath);
		
		return null;
	}
}