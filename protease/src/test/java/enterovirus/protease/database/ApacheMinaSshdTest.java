package enterovirus.protease.database;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Base64;

import org.junit.Test;

import org.apache.sshd.common.config.keys.AuthorizedKeyEntry;
import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator;
import org.apache.sshd.server.config.keys.DefaultAuthorizedKeysAuthenticator;

public class ApacheMinaSshdTest {
	
	@Test
	public void AuthorizedKeyEntryTest () throws IOException {
		
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
			
			/*
			 * If the format is wrong (no valid key), it will raises error (StreamCorruptedException extends IOException):
			 * > java.io.StreamCorruptedException: Failed (IllegalArgumentException) to parse key entry=wrong line: Bad format (no key data delimiter): line
			 */
			System.out.println("PublicKeyEntry.keyType: "+key.getKeyType());
			
			/*
			 * This library accept (in generally) all key data, e.g., `INVALIDKEY`.
			 * But it gives errors for special characters such as "_", e.g. `INVALID_KEY`.
			 * 
			 * It also changes the invalid keys a little bit.
			 * E.g., for `INVALIDKEY`, it will give back `INVALIDKEQ==`
			 */
			Base64.Encoder encoder = Base64.getEncoder();
			System.out.println("PublicKeyEntry.keyData: "+encoder.encodeToString(key.getKeyData()));
			
			/*
			 * For "forced commands"
			 * `command="./an-executable-script.sh arg1 arg2",no-port-forwarding,no-x11-forwarding,no-agent-forwarding,no-pty ssh-rsa AAAA....`
			 * 
			 * Key						Value
			 * `command`				`"./an-executable-script.sh`
			 * `no-agent-forwarding`	`true`
			 * `no-port-forwarding`		`true`
			 * `no-pty`					`true`
			 */
			System.out.println("AuthorizedKeyEntry.loginOptions");
			Map<String, String> loginOptions = key.getLoginOptions();
			for (Map.Entry<String, String> entry : loginOptions.entrySet()) {
				System.out.println(entry.getKey()+"\t\t\t"+entry.getValue());
			}
			
			/*
			 * Comments are the very last part. Typically an email address.
			 */
			System.out.println("AuthorizedKeyEntry.comment: "+key.getComment());
			
			/*
			 * Static method, result:
			 * /home/beta/.ssh
			 */
			System.out.println("PublicKeyEntry.getDefaultKeysFolderPath(): "+AuthorizedKeyEntry.getDefaultKeysFolderPath());
			
			System.out.println("======================");
		}
	}
	
	@Test
	public void defaultAuthorizedKeysAuthenticatorTest () throws IOException {
		
		/*
		 * Seems not quite useful for my current usage.
		 */
		
		File authorizedKeyFile = new File("/home/beta/Workspace/enterovirus-test/ssh_tests/.ssh/authorized_keys");
		List<AuthorizedKeyEntry> keys = AuthorizedKeyEntry.readAuthorizedKeys(authorizedKeyFile);
		
		/*
		 * The boolean strict=true makes sure that the `.ssh` folder has 0700 access
		 * while the `authorized_keys` file has 0600 access.
		 */
		PublickeyAuthenticator auth = new DefaultAuthorizedKeysAuthenticator(authorizedKeyFile, true);
	}

}
