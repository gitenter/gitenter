package enterovirus.enzymark.propertiesfile;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import enterovirus.gitar.GitBlob;
import enterovirus.gitar.wrap.CommitSha;

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
	
	public PropertiesFileParser(File repositoryDirectory, CommitSha commitSha, String relativeFilepath) throws PropertiesFileFormatException {
		
		try (GitBlob gitBlob = new GitBlob(repositoryDirectory, commitSha, relativeFilepath)) {
			
			/*
			 * TODO:
			 * What about blobContent cannot be convent to String?
			 */
			Reader targetReader = new StringReader(new String(gitBlob.getBlobContent()));
			
			PropertiesConfiguration config = new PropertiesConfiguration();
			config.load(targetReader);
			
			parseData(config);
		}
		catch (IOException e) {
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
		 * TODO:
		 * Extend to "include_patterns" which may include e.g., "doc/*.md"
		 * 
		 * TODO:
		 * Consider if this path is not included, then include everything.
		 */
		includePaths = config.getStringArray("include_paths");
		if (includePaths.length == 0) {
			throw new PropertiesFileFormatException("At least one \"include_paths\" should be provided.");
		}
	}
	
	public boolean isEnabledSystemwide () {
		return enableSystemwide;
	}
	
	public String[] getIncludePaths () {
		return includePaths;
	}
}
