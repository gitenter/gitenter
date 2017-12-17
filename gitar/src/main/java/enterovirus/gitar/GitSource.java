package enterovirus.gitar;

import java.io.File;

public class GitSource {

	/*
	 * This class is similar to javax.sql.DataSource
	 * (or org.springframework.jdbc.datasource.DriverManagerDataSource)
	 * in JDBC. Will think carefully how to implement it
	 * later.
	 */
	
	private String rootFolderPath;

	public void setRootFolderPath(String rootFolderPath) {
		this.rootFolderPath = rootFolderPath;
	}
	
	/*
	 * Here defines the folder structure to save the repositories
	 * in "/rootFolderPath/ownerName/repositoryName/" .
	 * 
	 * TODO:
	 * To change it to **bare** repository "repositoryName.git"
	 */
	public File getRepositoryDirectory (String ownerName, String repositoryName) {
		return new File(new File(new File(rootFolderPath, ownerName), repositoryName), ".git");
	}
}
