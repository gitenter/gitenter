package enterovirus.capsid.web;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.util.AntPathMatcher;

import enterovirus.capsid.database.DocumentRepository;
import enterovirus.capsid.domain.DocumentBean;

@RestController
@RequestMapping("/api")
public class ApiController {

	private DocumentRepository documentRepository;
	
	@Autowired
	public ApiController(DocumentRepository documentRepository) {
		this.documentRepository = documentRepository;
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
	public DocumentBean listUserRepositories(
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
	public DocumentBean listOrganizationRepositories(
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
	public DocumentBean getRepositoryInformation(
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
	public DocumentBean getDirectoryInformationInBranch(
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
	public DocumentBean getDirectoryInformationInCommit(
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
	public DocumentBean getDocumentContentInBranch(
			@PathVariable String organization,
			@PathVariable String repositoryName,
			@PathVariable String branchName,
			HttpServletRequest request) throws Exception {
		
		String wholePath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
	    String filePath = new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, wholePath);
		
		DocumentBean document = documentRepository.findDocument(organization, repositoryName, branchName, filePath);
		return document;
	}
	
	/**
	 * Get text file content (in commit)
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping(value="/organizations/{organization}/repositories/{repositoryName}/commits/{commitId}/files/**", method=RequestMethod.GET)
	public DocumentBean getDocumentContentInCommit(
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