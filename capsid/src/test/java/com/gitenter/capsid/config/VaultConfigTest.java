package com.gitenter.capsid.config;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponseSupport;

import com.gitenter.capsid.config.secret.PostgresSecretBean;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("sts")
public class VaultConfigTest {
	
	@Autowired private VaultOperations operations;

	@Test
	public void testVault() {
		
		/*
		 * TODO:
		 * 
		 * There seems two different ways to go:
		 * Vs Spring Vault core: https://projects.spring.io/spring-vault/#quick-start
		 * Vs Spring Cloud Vault: https://spring.io/guides/gs/vault-config/
		 * 
		 * Those two are not consistent. 
		 * Import libs for Spring Cloud Vault will cause Spring Vault Core to have 
		 * ApplicationContextError.
		 * 
		 * Current setup is to use Spring Vault core. It gives error
		 * > Unrecognized SSL message, plaintext connection?
		 * Looks like it tries to connect https://localhost:8200/ while the actual host
		 * is http://localhost:8200/ .
		 * Or just SSL configuration is missing 
		 * https://docs.spring.io/spring-vault/docs/current/reference/html/index.html#vault.client-ssl
		 */
		
//		VaultTemplate vaultTemplate = new VaultTemplate(VaultEndpoint.create("localhost", 8200),
//				new TokenAuthentication("00000000-0000-0000-0000-000000000000"));
		
		Map<String, String> data = new HashMap<String, String>();
	    data.put("password", "secretpassword");
	    operations.write("secret/myapp", data);

		PostgresSecretBean secret = new PostgresSecretBean();
		secret.setUsername("username");
		secret.setPassword("secretpassword");
		operations.write("secret/myapp", secret);

		VaultResponseSupport<PostgresSecretBean> response = operations.read("secret/myapp", PostgresSecretBean.class);
		assertEquals(response.getData().getUsername(), "username");
		assertEquals(response.getData().getPassword(), "secretpassword");

		operations.delete("secret/myapp");
	}
}
