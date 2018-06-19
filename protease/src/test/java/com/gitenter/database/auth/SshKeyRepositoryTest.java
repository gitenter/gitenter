package com.gitenter.database.auth;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.gitenter.database.auth.MemberRepository;
import com.gitenter.database.auth.SshKeyRepository;
import com.gitenter.domain.auth.MemberBean;
import com.gitenter.domain.auth.SshKeyBean;
import com.gitenter.protease.*;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = "production")
@ContextConfiguration(classes=ProteaseConfig.class)
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	DirtiesContextTestExecutionListener.class,
	TransactionalTestExecutionListener.class,
	DbUnitTestExecutionListener.class })
@DbUnitConfiguration(databaseConnection={"schemaAuthDatabaseConnection"})
public class SshKeyRepositoryTest {

	@Autowired MemberRepository memberRepository;
	@Autowired SshKeyRepository sshKeyRepository;
	
	/*
	 * Ignored, because otherwise the database complains error:
	 * > ERROR: duplicate key value violates unique constraint "ssh_key_key_key"
	 * 
	 * P.S Currently unique key is not setup yet.
	 */
	@Test
	@DatabaseSetup(connection="schemaAuthDatabaseConnection", value="classpath:dbunit/minimal-schema-auth.xml")
	public void test() throws Exception {
		
		MemberBean member = memberRepository.findById(1);
		
		Path publicKeyFile = Paths.get(System.getProperty("user.home"), ".ssh/id_rsa.pub");
		
		/*
		 * Read the first line, as it has only one line.
		 */
		String publicKeyLine = Files.lines(publicKeyFile).collect(Collectors.toList()).get(0);
		
		SshKeyBean sshKey = new SshKeyBean(publicKeyLine);
		sshKey.setMember(member);
		
		sshKeyRepository.saveAndFlush(sshKey, "user");
	}
	
	@Test
	public void testDummy () {
		
	}
}
