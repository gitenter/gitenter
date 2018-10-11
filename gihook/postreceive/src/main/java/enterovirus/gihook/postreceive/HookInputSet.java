package enterovirus.gihook.postreceive;

import java.io.File;
import java.io.IOException;

import com.gitenter.protease.source.GitSource;

public class HookInputSet {

	private File repositoryDirectory;
	
	private String branchName;
	private String oldSha;
	private String newSha;
	
	public HookInputSet(String userDir, String[] args) {
		
		this.repositoryDirectory = new File(userDir);
		
		this.branchName = args[2];
		this.oldSha = args[0];
		this.newSha = args[1];
		
		/* 
		 * Change Java working directory to the hook folder of
		 * the corresponding fake git repository.
		 * 
		 * Or may set it up in Eclipse's "Run configuration".
		 * 
		 * This is not needed for the final application (because 
		 * git hook is already running in the correctly directory
		 * location). But it is useful for testing purposes.
		 */
		System.setProperty("user.dir", repositoryDirectory.getAbsolutePath());
	}
	
	public File getRepositoryDirectory() {
		return repositoryDirectory;
	}

	public String getBranchName() {
		return branchName;
	}

	public String getOldSha() {
		return oldSha;
	}

	public String getNewSha() {
		return newSha;
	}
	
	public String getOrganizationName() {
		return GitSource.getBareRepositoryOrganizationName(repositoryDirectory);
	}
	
	public String getRepositoryName() throws IOException {
		return GitSource.getBareRepositoryName(repositoryDirectory);
	}
}
