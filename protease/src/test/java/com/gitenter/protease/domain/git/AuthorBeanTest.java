package com.gitenter.protease.domain.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.gitenter.protease.domain.git.AuthorBean;

public class AuthorBeanTest {

	@Test
	public void testSameEmailShareClassObject() {
		AuthorBean author1 = AuthorBean.getInstance("name", "email@email.com");
		AuthorBean author2 = AuthorBean.getInstance("name-not-the-same", "email@email.com");
		assertTrue(author1 == author2);
		assertEquals(author1.getName(), "name-not-the-same");
	}

	@Test
	public void testDifferentEmailDonnotShareClassObject() {
		AuthorBean author1 = AuthorBean.getInstance("name", "email@email.com");
		AuthorBean author2 = AuthorBean.getInstance("name", "email-different@email.com");
		assertFalse(author1 == author2);
	}
}
