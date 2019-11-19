package com.gitenter.protease.dao.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.TemporaryFolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.gitenter.protease.ProteaseConfig;
import com.gitenter.protease.domain.auth.UserBean;
import com.gitenter.protease.domain.auth.SshKeyBean;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(profiles = "minimal")
@ContextConfiguration(classes=ProteaseConfig.class)
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	DirtiesContextTestExecutionListener.class,
	TransactionalTestExecutionListener.class,
	DbUnitTestExecutionListener.class })
@DbUnitConfiguration(databaseConnection={
		"schemaAuthDatabaseConnection", 
		"schemaGitDatabaseConnection",
		"schemaTraceabilityDatabaseConnection",
		"schemaReviewDatabaseConnection"})
public class SshKeyRepositoryTest {

	@Autowired UserRepository userRepository;
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
	@DatabaseTearDown(connection="schemaAuthDatabaseConnection", value="classpath:dbunit/minimal/auth.xml")
	public void test() throws Exception {	
		UserBean user = userRepository.findById(1).get();
		assertEquals(user.getSshKeys().size(), 1);
		
		String keyType = "ssh-rsa";
		String keyData = "AAAAB3NzaC1yc2EAAAADAQABAAABAQCvYWPKDryb70LRP1tePi9h1q2vebxFIQZn3MlPbp4XYKP+t+t325BlMbj6Tnvx55nDR5Q6CwPOBz5ijdv8yUEuQ9aaR3+CNvOqjrs7iE2mO4HPiE+w9tppNhOF37a/ElVuoKQtTrP4hFyQbdISVCpvhXx9MZZcaq+A8aLbcrL1ggydXiLpof6gyb9UgduXx90ntbahI5JZgNTZfZSzzCRu7of/zZYKr4dQLiCFGrGDnSs+j7Fq0GAGKywRz27UMh9ChE+PVy8AEOV5/Mycula2KWRhKU/DWZF5zaeVE4BliQjKtCJwhJGRz52OdFc55ic7JoDcF9ovEidnhw+VNnN9";
		String comment = "comment";
		
		/*
		 * TODO:
		 * 
		 * Any way to not setup the double relationship (user-sshKey) manually?
		 * Notice that we cannot change "setUser()" to include "addSshKey()",
		 * as "setUser()" is also used for Hibernate.
		 */
		SshKeyBean sshKey = new SshKeyBean();
		sshKey.setBean(keyType+" "+keyData+" "+comment);
		sshKey.setUser(user);
		user.addSshKey(sshKey);
		
		sshKeyRepository.saveAndFlush(sshKey);
		
		UserBean refreshedUser = userRepository.findById(1).get();
		assertEquals(refreshedUser.getSshKeys().size(), 2);
		
		SshKeyBean refreshedSshKey = refreshedUser.getSshKeys().get(1);
		assertEquals(refreshedSshKey.getKeyType(), keyType);
		assertEquals(refreshedSshKey.getKeyData(), keyData);
		assertEquals(refreshedSshKey.getComment(), comment);
	}
}
