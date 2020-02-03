package com.gitenter.protease.domain.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class OrganizationUserMapBeanTest {

	@Test
	public void testJsonFormat() throws Exception {
		
		UserBean user = new UserBean();
		user.setId(123);
		user.setUsername("username");
		user.setPasswordHash("password_hash");
		user.setDisplayName("User Name");
		user.setEmail("user@email.com");
		user.setRegisterAt(new Date(0L));
		
		OrganizationBean organization = new OrganizationBean();
		organization.setId(456);
		organization.setName("org");
		organization.setDisplayName("Organization");
		
		OrganizationUserMapBean map = OrganizationUserMapBean.link(organization, user, OrganizationUserRole.ORDINARY_MEMBER);
		map.setId(789);
		
		ObjectMapper mapper = new ObjectMapper();
		assertEquals(
				mapper.writeValueAsString(map.getUserDetails()),
				"{\"id\":123,\"username\":\"username\",\"displayName\":\"User Name\",\"email\":\"user@email.com\",\"registerAt\":\"1970-01-01 12:00:00\","
				+ "\"mapId\":789,\"mapRole\":\"ORDINARY_MEMBER\"}");
	}
}
