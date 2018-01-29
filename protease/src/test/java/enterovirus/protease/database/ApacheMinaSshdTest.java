package enterovirus.protease.database;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import org.apache.sshd.common.config.keys.AuthorizedKeyEntry;

public class ApacheMinaSshdTest {
	
	@Test
	public void test () throws IOException {
		
		/*
		 * Initialization doesn't from constructor, but from a series of
		 * static methods, e.g., 
		 * public static List<AuthorizedKeyEntry> readAuthorizedKeys(URL url)
		 * public static List<AuthorizedKeyEntry> readAuthorizedKeys(File file)
		 * public static List<AuthorizedKeyEntry> readAuthorizedKeys(Path path, OpenOption... options)
		 */
		File authorizedKeyFile = new File("/home/beta/Workspace/enterovirus-test/ssh_tests/.ssh/authorized_keys");
		List<AuthorizedKeyEntry> keys = AuthorizedKeyEntry.readAuthorizedKeys(authorizedKeyFile);
		
		for (AuthorizedKeyEntry key : keys) {
			System.out.println(key);
		}
	}

}
