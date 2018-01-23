package enterovirus.protease.source;

import java.io.File;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GitSource {

	/*
	 * This class is similar to javax.sql.DataSource
	 * (or org.springframework.jdbc.datasource.DriverManagerDataSource)
	 * in JDBC. Will think carefully how to implement it
	 * later.
	 */
	private String rootFolderPath;

	/*
	 * Bare git repository at "/rootFolderPath/ownerName/repositoryName.git" .
	 */
	public File getBareRepositoryDirectory (String ownerName, String repositoryName) {
		return new File(new File(rootFolderPath, ownerName), repositoryName+".git");
	}
	
	public static String getBareRepositoryOrganizationName (File bareRepositoryDirectory) {
		String[] parts = bareRepositoryDirectory.getPath().split("/");
		return parts[parts.length-2];
	}
	
	public static String getBareRepositoryName (File bareRepositoryDirectory) {
		String[] parts = bareRepositoryDirectory.getPath().split("/");
		String gitFolder = parts[parts.length-1];
		return gitFolder.substring(0, gitFolder.length()-4);
	}
}
