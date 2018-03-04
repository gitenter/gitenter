package enterovirus.gihook.precommit;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import enterovirus.enzymark.propertiesfile.PropertiesFileFormatException;
import enterovirus.enzymark.propertiesfile.PropertiesFileParser;
import enterovirus.enzymark.traceanalyzer.TraceAnalyzerException;
import enterovirus.enzymark.traceanalyzer.TraceableDocument;
import enterovirus.enzymark.traceanalyzer.TraceableRepository;

@Service
public class CheckGitStatus {

	@Transactional
	public String apply (File repositoryDirectory)  throws IOException, GitAPIException {
		
		PropertiesFileParser propertiesFileParser;
		try {
			propertiesFileParser = new PropertiesFileParser(new File(repositoryDirectory, "gitenter.properties")); 
		}
		catch (PropertiesFileFormatException e) {
			return e.getMessage();
		}
		
		if (propertiesFileParser.isEnabledSystemwide() == false) {
			/*
			 * Parser check is bypassed.
			 */
			return null;
		}
		
		
		TraceableRepository traceableRepository;
		
		try {
			traceableRepository = new TraceableRepository(repositoryDirectory);
		
			/*
			 * Iterate all files under the list of "includePaths".
			 */
			String[] includePaths = propertiesFileParser.getIncludePaths();
			
			/*
			 * TODO:
			 * Integrate this part with "GitFolderStructure" iteration,
			 * or should we use some kind of filter, rather than this
			 * manual if-else?
			 * 
			 * TODO:
			 * Use MIME type rather than this manual extension check?
			 */
			Collection<File> includeFiles;
			if (includePaths.length == 0) {
				includeFiles = FileUtils.listFiles(repositoryDirectory, new String[]{"md"}, true);
			}
			else {
				includeFiles = new ArrayList<File>();
				for (String includePath : includePaths) {
					includeFiles.addAll(FileUtils.listFiles(new File(repositoryDirectory, includePath), new String[]{"md"}, true));
				}
			}
			
			for (File includeFile : includeFiles) {
				
				/*
				 * TODO:
				 * Should move this part to the "TraceableDocument" class.
				 */
				String relativeFilepath = repositoryDirectory.toURI().relativize(includeFile.toURI()).getPath();
				
				List<String> lines = Files.readAllLines(includeFile.toPath(), Charsets.UTF_8);
				String textContent = String.join("\n", lines);
				
				TraceableDocument traceableDocument = new TraceableDocument(traceableRepository, relativeFilepath, textContent);
				traceableRepository.addTraceableDocument(traceableDocument);
			}
		}
		catch (TraceAnalyzerException e) {
			return e.getMessage();
		}
		
		/*
		 * Parser check succeed.
		 */
		return null;
	}
}
