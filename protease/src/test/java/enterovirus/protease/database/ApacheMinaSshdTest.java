package enterovirus.protease.database;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.List;
import java.util.Map;
import java.util.Base64;

import org.junit.Test;

import org.apache.sshd.common.config.keys.AuthorizedKeyEntry;
import org.apache.sshd.common.config.keys.PublicKeyEntryResolver;
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
		List<AuthorizedKeyEntry> entries = AuthorizedKeyEntry.readAuthorizedKeys(authorizedKeyFile);
		
		for (AuthorizedKeyEntry entry : entries) {
			
			System.out.println(entry);
			System.out.println("-------------------");
			
			/*
			 * If the format is wrong (no valid key), it will raises error (StreamCorruptedException extends IOException):
			 * > java.io.StreamCorruptedException: Failed (IllegalArgumentException) to parse key entry=wrong line: Bad format (no key data delimiter): line
			 */
			System.out.println("PublicKeyEntry.keyType: "+entry.getKeyType());
			
			/*
			 * This library accept (in generally) all key data, e.g., `INVALIDKEY`.
			 * But it gives errors for special characters such as "_", e.g. `INVALID_KEY`.
			 * 
			 * It also changes the invalid keys a little bit.
			 * E.g., for `INVALIDKEY`, it will give back `INVALIDKEQ==`
			 */
			Base64.Encoder encoder = Base64.getEncoder();
			System.out.println("PublicKeyEntry.keyData: "+encoder.encodeToString(entry.getKeyData()));
			
			/*
			 * For "forced commands"
			 * `command="./an-executable-script.sh arg1 arg2",no-port-forwarding,no-x11-forwarding,no-agent-forwarding,no-pty ssh-rsa AAAA....`
			 * 
			 * Key						Value
			 * `command`				`"./an-executable-script.sh`
			 * `no-agent-forwarding`	`true`
			 * `no-port-forwarding`		`true`
			 * `no-pty`					`true`
			 * 
			 * For space in command, Mina SSHD 1.7.0 seems have a bug
			 * (I reported it at https://issues.apache.org/jira/projects/SSHD/issues/SSHD-796?filter=allopenissues)
			 * But for writing to the file there's no problem.
			 */
			System.out.println("AuthorizedKeyEntry.loginOptions");
			Map<String, String> loginOptions = entry.getLoginOptions();
			for (Map.Entry<String, String> mapEntry : loginOptions.entrySet()) {
				System.out.println(mapEntry.getKey()+"\t\t\t"+mapEntry.getValue());
			}
			
			/*
			 * Comments are the very last part. Typically an email address.
			 */
			System.out.println("AuthorizedKeyEntry.comment: "+entry.getComment());
			
			/*
			 * Static method, result:
			 * /home/beta/.ssh
			 */
			System.out.println("PublicKeyEntry.getDefaultKeysFolderPath(): "+AuthorizedKeyEntry.getDefaultKeysFolderPath());
			
			System.out.println("======================");
		}
	}
	
	@Test
	public void publicKeyTest () throws IOException,GeneralSecurityException {
		
		/*
		 * Should do. This makes sure that the user input keys are valid.
		 */
		
		File authorizedKeyFile = new File("/home/beta/Workspace/enterovirus-test/ssh_tests/.ssh/authorized_keys");
		List<AuthorizedKeyEntry> entries = AuthorizedKeyEntry.readAuthorizedKeys(authorizedKeyFile);
		
		/*
		 * The fake key `ssh-rsa INVALIDKEY` will raise error:
		 * > java.io.EOFException: Premature EOF - expected=550846508, actual=3
		 */
		List<PublicKey> keys = AuthorizedKeyEntry.resolveAuthorizedKeys(PublicKeyEntryResolver.FAILING, entries);
		
		for (PublicKey key : keys) {
			
			/*
			 * RSA, ...
			 */
			System.out.println("PublicKey.getAlgorithm(): "+key.getAlgorithm());
			
			/*
			 * X.509, ...
			 */
			System.out.println("PublicKey.getFormat(): "+key.getFormat());
			
			System.out.println("PublicKey.getEncoded(): "+key.getEncoded());
		}
	}
	
	@Test
	public void authorizedKeysAuthenticatorTest () throws IOException {
		
		/*
		 * Seems not quite useful for my current usage.
		 */
		
		File authorizedKeyFile = new File("/home/beta/Workspace/enterovirus-test/ssh_tests/.ssh/authorized_keys");
		List<AuthorizedKeyEntry> entries = AuthorizedKeyEntry.readAuthorizedKeys(authorizedKeyFile);
		
		/*
		 * The boolean strict=true makes sure that the `.ssh` folder has 0700 access
		 * while the `authorized_keys` file has 0600 access.
		 */
		PublickeyAuthenticator auth = new DefaultAuthorizedKeysAuthenticator(authorizedKeyFile, true);
	}

}
