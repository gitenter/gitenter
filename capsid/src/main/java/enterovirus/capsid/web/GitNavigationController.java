package enterovirus.capsid.web;

import java.io.File;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerMapping;

import enterovirus.enzymark.htmlgenerator.DesignDocumentHtmlGenerator;
import enterovirus.enzymark.propertiesfile.PropertiesFileFormatException;
import enterovirus.enzymark.propertiesfile.PropertiesFileParser;
import enterovirus.gitar.GitSource;
import enterovirus.gitar.wrap.*;
import enterovirus.protease.database.*;
import enterovirus.protease.domain.*;

@Controller
public class GitNavigationController {	

	@Autowired private BlobGitDAO blobGitDAO;
	@Autowired private RepositoryRepository repositoryRepository;
	@Autowired private RepositoryGitDAO repositoryGitDAO;
	@Autowired private CommitRepository commitRepository;
	@Autowired private CommitGitDAO commitGitDAO;
	@Autowired private DocumentRepository documentRepository;
	@Autowired private GitSource gitSource;
	
	private String getWildcardValue (HttpServletRequest request) {
		
		String wholePath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		String wildcard = new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, wholePath);
		
		return wildcard;
	}
	
	/*
	 * TODO:
	 * Should the default shown branch be set up by the user?
	 */
	private BranchName getDefaultBranchName (String branch) {
		
		BranchName branchName;
		if (branch.equals("")) {
			/*
			 * Because "@ModelAttribute("branch") String branch"
			 * will get "" rather than null if missing.
			 */
			branchName = new BranchName("master");
		}
		else {
			branchName = new BranchName(branch);
		}
		
		return branchName;
	}
	
	/*************************************************************************/
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/branches/{branchName}/commits", method=RequestMethod.GET)
	public String showCommitList (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			@PathVariable BranchName branchName,
			HttpServletRequest request,
			Model model) throws Exception {
		
		model.addAttribute("branch", branchName.getName());
		
		RepositoryBean repository = repositoryRepository.findById(repositoryId);
		repositoryGitDAO.loadCommitLog(repository, branchName);
		repositoryGitDAO.loadBranchNames(repository);
		
		model.addAttribute("organization", repository.getOrganization());
		model.addAttribute("repository", repository);
		
		/*
		 * TODO:
		 * Show the status of the commit (valid/invalid/ignored).
		 * 
		 * TODO:
		 * Connect the git user (name and email) to the user of
		 * this website.
		 */
		
		return "git-navigation/commit-list";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/commits", method=RequestMethod.GET)
	public String showCommitListRedirect (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			@ModelAttribute("branch") BranchName branchName) {
		
		return "redirect:/organizations/"+organizationId+"/repositories/"+repositoryId+"/branches/"+branchName.getName()+"/commits";
	}

	/*************************************************************************/
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/commits/{commitSha}", method=RequestMethod.GET)
	public String showFolderStructureByCommit (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			@PathVariable CommitSha commitSha,
			HttpServletRequest request,
			Model model) throws Exception {
		
		model.addAttribute("shaChecksumHash", commitSha.getShaChecksumHash());
		
		CommitBean commit = commitRepository.findByCommitSha(commitSha);
		return showFolderStructure(commit, request, model);
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/branches/{branchName}", method=RequestMethod.GET)
	public String showFolderStructureByBranch (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			@PathVariable BranchName branchName,
			HttpServletRequest request,
			Model model) throws Exception {
		
		model.addAttribute("branch", branchName.getName());
		
		CommitBean commit = commitRepository.findByRepositoryIdAndBranch(repositoryId, branchName);
		return showFolderStructure(commit, request, model);
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}", method=RequestMethod.GET)
	public String showFolderStructureDefault (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			@ModelAttribute("branch") String branch,
			HttpServletRequest request) {
		
		BranchName branchName = getDefaultBranchName(branch);
		return "redirect:/organizations/"+organizationId+"/repositories/"+repositoryId+"/branches/"+branchName.getName();
	}
	
	private String showFolderStructure (
			CommitBean commit, 
			HttpServletRequest request, 
			Model model) throws Exception {

		String currentUrl = request.getRequestURL().toString();
		model.addAttribute("currentUrl", currentUrl);
		
		RepositoryBean repository = commit.getRepository();
		repositoryGitDAO.loadBranchNames(repository);
		
		model.addAttribute("organization", repository.getOrganization());
		model.addAttribute("repository", repository);
		
		if (commit instanceof CommitValidBean) {
			
			/*
			 * TODO:
			 * Consider move this part to "commitGitDAO.loadFolderStructure()".
			 * Currently cannot be done, because "PropertiesFileParser" is in enzymark.
			 * But enzymark depends on protease (mainly for using the domain models).
			 * But CommitGitDAO is in protease. So if included, it will cause loop 
			 * dependencies.
			 * Consider a better way how to set up which content belongs to which package.
			 */
			String organizationName = commit.getRepository().getOrganization().getName();
			String repositoryName = commit.getRepository().getName();
			
			File repositoryDirectory = gitSource.getBareRepositoryDirectory(organizationName, repositoryName);
			CommitSha commitSha = new CommitSha(commit.getShaChecksumHash());
			
			PropertiesFileParser propertiesFileParser;
			try {
				propertiesFileParser = new PropertiesFileParser(repositoryDirectory, commitSha, "enterovirus.properties"); 
			}
			catch (PropertiesFileFormatException e) {
				/*
				 * It shouldn't happen if the post-receive parser does the correct job.
				 */
				throw new RuntimeException("PropertiesFileFormatException in CommitGitDAO");
			}
			String[] includePaths = propertiesFileParser.getIncludePaths();
			
			commitGitDAO.loadFolderStructure((CommitValidBean)commit, includePaths);
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
	
	/*************************************************************************/

	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/commits/{commitSha}/blobs/directories/**", method=RequestMethod.GET)
	public void showBlobContentByCommit (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			@PathVariable CommitSha commitSha,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String relativeFilepath = getWildcardValue(request);
		RepositoryBean repository = repositoryRepository.findById(repositoryId);
		BlobBean blob = blobGitDAO.find(repository.getOrganization().getName(), repository.getName(), commitSha, relativeFilepath);

		writeToOutputStream(blob, response);
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/branches/{branchName}/blobs/directories/**", method=RequestMethod.GET)
	public void showBlobContentByBranch (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			@PathVariable BranchName branchName,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String relativeFilepath = getWildcardValue(request);
		RepositoryBean repository = repositoryRepository.findById(repositoryId);
		BlobBean blob = blobGitDAO.find(repository.getOrganization().getName(), repository.getName(), branchName, relativeFilepath);

		writeToOutputStream(blob, response);
	}
	
	private void writeToOutputStream (BlobBean blob, HttpServletResponse response) throws Exception {

		if (!blob.getMimeType().equals("text/markdown")) {
			
			response.setContentType(blob.getMimeType());
			OutputStream outputStream = response.getOutputStream();
			outputStream.write(blob.getBlobContent());
			outputStream.close();
		}
	}
	
	/*************************************************************************/
	
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
			@PathVariable CommitSha commitSha,
			HttpServletRequest request,
			Model model) throws Exception {
		
		model.addAttribute("shaChecksumHash", commitSha.getShaChecksumHash());
		
		String relativeFilepath = getWildcardValue(request);
		model.addAttribute("relativeFilepath", relativeFilepath);
		DocumentBean document = documentRepository.findByCommitShaAndRelativeFilepath(commitSha, relativeFilepath);
		
		return showDocumentContent(document, request, model);
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/branches/{branchName}/documents/directories/**", method=RequestMethod.GET)
	public String showDocumentContentByBranch (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			@PathVariable BranchName branchName,
			HttpServletRequest request,
			Model model) throws Exception {
		
		model.addAttribute("branch", branchName.getName());
		
		String relativeFilepath = getWildcardValue(request);
		model.addAttribute("relativeFilepath", relativeFilepath);
		DocumentBean document = documentRepository.findByRepositoryIdAndBranchAndRelativeFilepath(repositoryId, branchName, relativeFilepath);
		
		return showDocumentContent(document, request, model);
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/documents/directories/**", method=RequestMethod.GET)
	public String showDocumentContentDefault (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			@ModelAttribute("branch") String branch,
			HttpServletRequest request) {
		
		BranchName branchName = getDefaultBranchName(branch);
		String filepath = getWildcardValue(request);
		return "redirect:/organizations/"+organizationId+"/repositories/"+repositoryId+"/branches/"+branchName.getName()+"/documents/directories/"+filepath;
	}
	
	private String showDocumentContent (
			DocumentBean document, 
			HttpServletRequest request,
			Model model) throws Exception {
		
		CommitBean commit = document.getCommit();
		RepositoryBean repository = commit.getRepository();
		repositoryGitDAO.loadBranchNames(repository);
		
		model.addAttribute("document", document);
		model.addAttribute("organization", repository.getOrganization());
		model.addAttribute("repository", repository);
		
		DesignDocumentHtmlGenerator contentParser = new DesignDocumentHtmlGenerator(document);
		model.addAttribute("content", contentParser.getHtml());
			
		return "git-navigation/document";
	}
}