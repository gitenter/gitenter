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
import enterovirus.gitar.*;

@RestController
public class MainController {

	private TextFileRepository textFileRepository;
	
	@Autowired
	public MainController(TextFileRepository textFileRepository) {
		this.textFileRepository = textFileRepository;
	}	
	
	@RequestMapping("/")
	public String index() {
		return Main.hello()+"\n"+"Hello enterovirus capsid!";
	}
	
	@RequestMapping(value="/{username}/{repositoryName}/blob/{branchName}/**", method=RequestMethod.GET)
	public TextFileBean showTextFile(
			@PathVariable String username,
			@PathVariable String repositoryName,
			@PathVariable String branchName,
			HttpServletRequest request) throws Exception {
		
		String wholePath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		AntPathMatcher apm = new AntPathMatcher();
	    String filePath = apm.extractPathWithinPattern(bestMatchPattern, wholePath);
		
		TextFileBean textFile = textFileRepository.findTextFile(username, repositoryName, branchName, filePath);
		return textFile;
	}
}