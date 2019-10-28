package com.gitenter.enzymark.configfile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;

import com.gitenter.enzymark.configfile.bean.GitEnterConfigBean;
import com.gitenter.gitar.GitBareRepository;
import com.gitenter.gitar.GitCommit;
import com.gitenter.gitar.GitHistoricalFile;
import com.gitenter.gitar.GitRepository;

public class ConfigYamlParser {

	static GitEnterConfigBean parse(String yamlContent) throws ConfigFileFormatException {
		
		Constructor constructor = new Constructor(GitEnterConfigBean.class);
		Yaml yaml = new Yaml(constructor);
		
		GitEnterConfigBean gitEnterConfig;
		try {
			gitEnterConfig = yaml.load(yamlContent);
		}
		catch (YAMLException e) {
			throw new ConfigFileFormatException(e.getMessage());
		}
		return gitEnterConfig;
		
		/*
		 * TODO:
		 * Should check if `markdown` traceability scan path is
		 * inside of the `documents` scan path.
		 */
	}
	
	static GitEnterConfigBean parse(File file) throws ConfigFileFormatException, IOException {
		String yamlContent = String.join("\n", Files.readAllLines(file.toPath()));
		return parse(yamlContent);
	}
	
	public static GitEnterConfigBean parse(
			File repositoryDirectory, 
			String sha, 
			String relativeFilepath) throws ConfigFileFormatException, IOException {
		String yamlContent = getFileContent(repositoryDirectory, sha, relativeFilepath);
		return parse(yamlContent);
	}
	
	private static String getFileContent(
			File repositoryDirectory, 
			String sha, 
			String relativeFilepath) throws ConfigFileFormatException, IOException {
		
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
			throw new ConfigFileFormatException(e.getMessage());
		}
	}
}
