package com.gitenter.protease.domain.auth;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.apache.sshd.common.config.keys.AuthorizedKeyEntry;
import org.apache.sshd.common.config.keys.PublicKeyEntryResolver;
import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator;
import org.apache.sshd.server.config.keys.DefaultAuthorizedKeysAuthenticator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.gitenter.protease.domain.auth.SshKeyBean;

public class SshKeyBeanTest {
	
	@Rule public TemporaryFolder folder = new TemporaryFolder();
	
	private String keyType;
	private String keyData;
	private String comment;
	
	private File authorizedKeyFile;
	
	@Before
	public void setUp() throws IOException {
		
		keyType = "ssh-rsa";
		keyData = "AAAAB3NzaC1yc2EAAAADAQABAAABAQCvYWPKDryb70LRP1tePi9h1q2vebxFIQZn3MlPbp4XYKP+t+t325BlMbj6Tnvx55nDR5Q6CwPOBz5ijdv8yUEuQ9aaR3+CNvOqjrs7iE2mO4HPiE+w9tppNhOF37a/ElVuoKQtTrP4hFyQbdISVCpvhXx9MZZcaq+A8aLbcrL1ggydXiLpof6gyb9UgduXx90ntbahI5JZgNTZfZSzzCRu7of/zZYKr4dQLiCFGrGDnSs+j7Fq0GAGKywRz27UMh9ChE+PVy8AEOV5/Mycula2KWRhKU/DWZF5zaeVE4BliQjKtCJwhJGRz52OdFc55ic7JoDcF9ovEidnhw+VNnN9";
		comment = "comment";
		
		String username = "username";
		String command = "command=\"./git-authorization.sh "+username+"\",no-port-forwarding,no-x11-forwarding,no-agent-forwarding,no-pty";
		
		authorizedKeyFile = folder.newFile("authorized_keys");
		
		byte[] encoded = (command+" "+keyType+" "+keyData+" "+comment+"\n").getBytes();
		Files.write(Paths.get(authorizedKeyFile.getAbsolutePath()), encoded);
	}

	@Test
	public void testSetAndGet() throws GeneralSecurityException, IOException {
		
		SshKeyBean sshKey = new SshKeyBean();
		sshKey.setBean(keyType+" "+keyData+" "+comment);
		
		assertEquals(sshKey.getKeyType(), keyType);
		assertEquals(sshKey.getKeyDataToString(), keyData);
		assertEquals(sshKey.getComment(), comment);
	}
	
	/*
	 * These tests are mostly for understanding how Apache Mina SSHD works.
	 */
	@Test
	public void testApacheMinaSshdAuthorizedKeyEntry() throws IOException {
		
		assertEquals(AuthorizedKeyEntry.getDefaultKeysFolderPath().toFile().getAbsolutePath(), new File(System.getProperty("user.home"), ".ssh").getAbsolutePath());
		
		/*
		 * Initialization doesn't from constructor, but from a series of
		 * static methods, e.g., 
		 * public static List<AuthorizedKeyEntry> readAuthorizedKeys(URL url)
		 * public static List<AuthorizedKeyEntry> readAuthorizedKeys(File file)
		 * public static List<AuthorizedKeyEntry> readAuthorizedKeys(Path path, OpenOption... options)
		 */
		List<AuthorizedKeyEntry> entries = AuthorizedKeyEntry.readAuthorizedKeys(authorizedKeyFile);
		
		assertEquals(entries.size(), 1);
		AuthorizedKeyEntry entry = entries.get(0);
		
		/*
		 * If the format is wrong (no valid key), it will raises error (StreamCorruptedException extends IOException):
		 * > java.io.StreamCorruptedException: Failed (IllegalArgumentException) to parse key entry=wrong line: Bad format (no key data delimiter): line
		 */
		assertEquals(entry.getKeyType(), keyType);
		
		/*
		 * This library accept (in generally) all key data, e.g., `INVALIDKEY`.
		 * But it gives errors for special characters such as "_", e.g. `INVALID_KEY`.
		 * 
		 * It also changes the invalid keys a little bit.
		 * E.g., for `INVALIDKEY`, it will give back `INVALIDKEQ==`
		 */
		Base64.Encoder encoder = Base64.getEncoder();
		assertEquals(encoder.encodeToString(entry.getKeyData()), keyData);

		assertEquals(entry.getComment(), comment);

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
		Map<String, String> loginOptions = entry.getLoginOptions();
		assertEquals(loginOptions.size(), 1);
		assertEquals(loginOptions.get("command"), "\"./git-authorization.sh");
	}
	
	@Test
	public void testApacheMinaSshdPublicKey() throws IOException, GeneralSecurityException {
		
		List<AuthorizedKeyEntry> entries = AuthorizedKeyEntry.readAuthorizedKeys(authorizedKeyFile);
	
		/*
		 * The fake key `ssh-rsa INVALIDKEY` will raise error:
		 * > java.io.EOFException: Premature EOF - expected=550846508, actual=3
		 */
		List<PublicKey> keys = AuthorizedKeyEntry.resolveAuthorizedKeys(PublicKeyEntryResolver.FAILING, entries);
		
		assertEquals(keys.size(), 1);
		PublicKey key = keys.get(0);
		
		assertEquals(key.getAlgorithm(), "RSA");
		assertEquals(key.getFormat(), "X.509");
		assertTrue(key.getEncoded() instanceof byte[]);		
	}
	
	@Test
	public void testApacheMinaSshdPublickeyAuthenticator() throws IOException, GeneralSecurityException {
		
		/*
		 * The boolean strict=true makes sure that the `.ssh` folder has 0700 access
		 * while the `authorized_keys` file has 0600 access.
		 */
		PublickeyAuthenticator auth = new DefaultAuthorizedKeysAuthenticator(authorizedKeyFile, true);
		
		assertEquals(auth.toString(), authorizedKeyFile.getAbsolutePath());
	}
}
