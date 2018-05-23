package enterovirus.protease.source;

import java.io.File;

import lombok.Getter;
import lombok.Setter;

@Getter
public class SshSource {

	/*
	 * This class is similar to javax.sql.DataSource
	 * (or org.springframework.jdbc.datasource.DriverManagerDataSource)
	 * in JDBC. Will think carefully how to implement it
	 * later.
	 */
	
	private String sshFolderPath;
	
	public File getAuthorizedKeysFilepath () {
		return new File(new File(sshFolderPath), "authorized_keys");
	}
	
	public void setSshFolderPath(File file) {
		this.sshFolderPath = file.getAbsolutePath();
	}

	public void setSshFolderPath(String sshFolderPath) {
		this.sshFolderPath = sshFolderPath;
	}
}
