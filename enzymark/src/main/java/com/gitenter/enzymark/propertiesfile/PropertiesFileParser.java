package com.gitenter.enzymark.propertiesfile;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.jgit.api.errors.GitAPIException;

import com.gitenter.gitar.GitBareRepository;
import com.gitenter.gitar.GitCommit;
import com.gitenter.gitar.GitHistoricalFile;
import com.gitenter.gitar.GitRepository;

/*
 * The config file is read using the Properties file:
 * https://docs.oracle.com/javase/tutorial/essential/environment/properties.html
 * https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html
 * http://www.mkyong.com/java/java-properties-file-examples/
 * https://commons.apache.org/proper/commons-configuration/userguide/howto_properties.html
 * 
 * Example how to write a conf file:
 * /etc/postgresql/9.5/main/postgresql.conf
 */
public class PropertiesFileParser {
	
	private boolean enableSystemwide;
	private String[] includePaths;

	/*
	 * TODO:
	 * 
	 * Current has warning
	 * 
	 * > log4j:WARN No appenders could be found for logger (org.apache.commons.configuration.PropertiesConfiguration).
	 * > log4j:WARN Please initialize the log4j system properly.
	 */
	public PropertiesFileParser(File propertiesFile) throws PropertiesFileFormatException {
		
		try {
			Configuration config = new PropertiesConfiguration(propertiesFile);
			if (config.isEmpty()) {
				/*
				 * If the properties file does not exist, then this entire system
				 * is turned off.
				 */
				enableSystemwide = false;
				return;	
			}
			
			parseData(config);
		}
		catch (ConfigurationException e) {
			/*
			 * TODO:
			 * This is for "Error while loading the properties file".
			 * That doesn't include if the file doesn't exist.
			 * Then what does that exactly for?
			 */
		}
	}
	
	public PropertiesFileParser(File repositoryDirectory, String sha, String relativeFilepath) throws PropertiesFileFormatException {
		
		try {
			String fileContent = getFileContent(repositoryDirectory, sha, relativeFilepath);
			Reader targetReader = new StringReader(fileContent);
			
			PropertiesConfiguration config = new PropertiesConfiguration();
			config.load(targetReader);
			
			parseData(config);
		}
		catch (PropertiesFileLoadException e) {
			enableSystemwide = false;
		}
		catch (ConfigurationException e) {
			/*
			 * TODO:
			 * This is for "Error while loading the properties file".
			 * That doesn't include if the file doesn't exist.
			 * Then what does that exactly for?
			 */
		}
	}
	
	private String getFileContent(File repositoryDirectory, String sha, String relativeFilepath) throws PropertiesFileLoadException {
		
		try {
			GitRepository repository = GitBareRepository.getInstance(repositoryDirectory);
			GitCommit commit = repository.getCommit(sha);
			GitHistoricalFile file = commit.getFile(relativeFilepath);
			
			/*
			 * TODO:
			 * What about blobContent cannot be convent to String?
			 */
			return new String(file.getBlobContent());
		}
		/*
		 * TODO:
		 * Remove the dependency leaking on JGit exception.
		 */
		catch (GitAPIException e) {
			throw new PropertiesFileLoadException(e.getMessage());
		}
		catch (IOException e) {
			/*
			 * TODO:
			 * 
			 * That includes the case 
			 * (1) The properties file doesn't exist (FileNotFoundException).
			 * (2) Several JGit raises exceptions of ".git" folder related,
			 * see e.g. http://download.eclipse.org/jgit/docs/jgit-2.0.0.201206130900-r/apidocs/org/eclipse/jgit/revwalk/RevWalk.html#parseCommit(org.eclipse.jgit.lib.AnyObjectId)
			 * 
			 * Actually only the first case (FileNotFoundException) I want
			 * to turn off the system, but for other cases I should raise other
			 * exceptions.
			 */
			throw new PropertiesFileLoadException(e.getMessage());
		}
	}
	
	private void parseData (Configuration config) throws PropertiesFileFormatException {
		
		String propName;
		String propValue;
		
		propName = "enable_systemwide";
		propValue = config.getString(propName);
		if (propValue == null) {
			throw new PropertiesFilePropNameException(propName);
		}
		if (propValue.equals("on")) {
			enableSystemwide = true;
		}
		else if (propValue.equals("off")) {
			enableSystemwide = false;
		} 
		else {
			throw new PropertiesFilePropValueException(propName, propValue);
		}
		
		/*
		 * Can have format:
		 * 
		 * > include_paths = folder1,folder2,folder3 
		 * 
		 * Or
		 * 
		 * > include_paths = folder1
		 * > include_paths = folder2
		 * > include_paths = folder3
		 * 
		 * At least one path should be provided.
		 * 
		 * NOTE:
		 * if there is no path provided, this will give "includePaths"
		 * equals empty String "new String[]{}". Then based on the implementation
		 * in GitFolderStrucuture, no filter will be setup and everything will
		 * be returned.
		 * 
		 * TODO:
		 * Extend to "include_patterns" which may include e.g., "doc/*.md"
		 */
		includePaths = config.getStringArray("include_paths");
	}
	
	public boolean isEnabledSystemwide () {
		return enableSystemwide;
	}
	
	public String[] getIncludePaths () {
		return includePaths;
	}
	
	class PropertiesFileLoadException extends Exception {

		private static final long serialVersionUID = 1L;
		
		public PropertiesFileLoadException(String message) {
			super(message);
		}
	}
}
