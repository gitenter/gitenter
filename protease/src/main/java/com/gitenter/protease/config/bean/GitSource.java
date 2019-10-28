package com.gitenter.protease.config.bean;

import java.io.File;
import java.io.IOException;

import lombok.Getter;

@Getter
public class GitSource {

	/*
	 * This class is similar to javax.sql.DataSource
	 * (or org.springframework.jdbc.datasource.DriverManagerDataSource)
	 * in JDBC. Will think carefully how to implement it
	 * later.
	 */
	final private String rootFolderPath;
	
	public GitSource(String rootFolderPath) {
		this.rootFolderPath = rootFolderPath;
	}
	
	public GitSource(File rootFile) {
		this.rootFolderPath = rootFile.getPath();
	}
	
	public File getRootDirectory() {
		return new File(rootFolderPath);
	}

	public File getOrganizationDirectory(String ownerName) {
		return new File(rootFolderPath, ownerName);
	}
	
	public boolean mkdirOrganizationDirectory(String ownerName) throws IOException {
		
		File directory = new File(rootFolderPath, ownerName);
		boolean result = directory.mkdir();
		
		return result;
	}
	
	/*
	 * Bare git repository at "/rootFolderPath/ownerName/repositoryName.git" .
	 */
	public File getBareRepositoryDirectory(String ownerName, String repositoryName) {
		return new File(new File(rootFolderPath, ownerName), repositoryName+".git");
	}
	
//	public boolean mkdirBareRepositoryDirectory (String ownerName, String repositoryName) throws IOException {
//		
//		File directory = new File(new File(rootFolderPath, ownerName), repositoryName+".git");
//		boolean result = directory.mkdir();
//		
//		return result;
//	}
	
	public static String getBareRepositoryOrganizationName(File bareRepositoryDirectory) {
		String[] parts = bareRepositoryDirectory.getPath().split("/");
		return parts[parts.length-2];
	}
	
	public static String getBareRepositoryName(File bareRepositoryDirectory) throws IOException {
		String[] parts = bareRepositoryDirectory.getPath().split("/");
		String gitFolder = parts[parts.length-1];
		
		if (!gitFolder.substring(gitFolder.length()-4, gitFolder.length()).equals(".git")) {
			throw new IOException(bareRepositoryDirectory+" is not a bare repository directory");
		}
		return gitFolder.substring(0, gitFolder.length()-4);
	}
}
