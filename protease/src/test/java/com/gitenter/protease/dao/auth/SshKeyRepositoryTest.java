package com.gitenter.protease.dao.auth;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.gitenter.protease.ProteaseConfig;
import com.gitenter.protease.dao.auth.MemberRepository;
import com.gitenter.protease.dao.auth.SshKeyRepository;
import com.gitenter.protease.domain.auth.MemberBean;
import com.gitenter.protease.domain.auth.SshKeyBean;
import com.gitenter.protease.source.SshSource;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = "minimal")
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
	
	@Rule public TemporaryFolder folder = new TemporaryFolder();
	
	/*
	 * TODO:
	 * 
	 * Every time after the database setup (remove the table and re-create them
	 * again), this test will raise the following error:
	 * 
	 * > WARN [main] (SqlExceptionHelper.java:129) - SQL Error: 0, SQLState: 23505
	 * > ERROR [main] (SqlExceptionHelper.java:131) - ERROR: duplicate key value violates unique constraint "ssh_key_pkey"
	 * > Detail: Key (id)=(1) already exists.
	 * 
	 * But the second run it will be fine. Should investigate why.
	 */
	@Test
	@Transactional
	@DatabaseSetup(connection="schemaAuthDatabaseConnection", value="classpath:dbunit/minimal/auth.xml")
	public void test() throws Exception {
		
		File file = folder.newFile("authorized_keys");
		
		SshSource sshSource = mock(SshSource.class);
		when(sshSource.getAuthorizedKeysFilepath()).thenReturn(file);
		ReflectionTestUtils.setField(sshKeyRepository, "sshSource", sshSource);
		
		MemberBean member = memberRepository.findById(1).get();
		assertEquals(member.getSshKeys().size(), 1);
		
		String keyType = "ssh-rsa";
		String keyData = "AAAAB3NzaC1yc2EAAAADAQABAAABAQCvYWPKDryb70LRP1tePi9h1q2vebxFIQZn3MlPbp4XYKP+t+t325BlMbj6Tnvx55nDR5Q6CwPOBz5ijdv8yUEuQ9aaR3+CNvOqjrs7iE2mO4HPiE+w9tppNhOF37a/ElVuoKQtTrP4hFyQbdISVCpvhXx9MZZcaq+A8aLbcrL1ggydXiLpof6gyb9UgduXx90ntbahI5JZgNTZfZSzzCRu7of/zZYKr4dQLiCFGrGDnSs+j7Fq0GAGKywRz27UMh9ChE+PVy8AEOV5/Mycula2KWRhKU/DWZF5zaeVE4BliQjKtCJwhJGRz52OdFc55ic7JoDcF9ovEidnhw+VNnN9";
		String comment = "comment";
		String command = "command=\"./git-authorization.sh "+member.getUsername()+"\",no-port-forwarding,no-x11-forwarding,no-agent-forwarding,no-pty";
		
		/*
		 * TODO:
		 * 
		 * Any way to not setup the double relationship (member-sshKey) manually?
		 * Notice that we cannot change "setMember()" to include "addSshKey()",
		 * as "setMember()" is also used for Hibernate.
		 */
		SshKeyBean sshKey = new SshKeyBean();
		sshKey.setBean(keyType+" "+keyData+" "+comment);
		sshKey.setMember(member);
		member.addSshKey(sshKey);
		
		sshKeyRepository.saveAndFlush(sshKey);
		
		byte[] encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
		assertEquals(new String(encoded), command+" "+keyType+" "+keyData+" "+comment+"\n");
		
		MemberBean refreshedMember = memberRepository.findById(1).get();
		assertEquals(refreshedMember.getSshKeys().size(), 2);
		
		SshKeyBean refreshedSshKey = refreshedMember.getSshKeys().get(1);
		assertEquals(refreshedSshKey.getKeyType(), keyType);
		assertEquals(refreshedSshKey.getKeyDataToString(), keyData);
		assertEquals(refreshedSshKey.getComment(), comment);
	}
}
