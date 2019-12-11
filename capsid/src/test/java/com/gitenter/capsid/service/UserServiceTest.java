package com.gitenter.capsid.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.gitenter.capsid.dto.UserProfileDTO;
import com.gitenter.capsid.dto.UserRegisterDTO;
import com.gitenter.capsid.service.exception.UserNotExistException;
import com.gitenter.protease.dao.auth.UserRepository;
import com.gitenter.protease.dao.auth.OrganizationUserMapRepository;
import com.gitenter.protease.dao.auth.OrganizationRepository;
import com.gitenter.protease.dao.auth.SshKeyRepository;
import com.gitenter.protease.domain.auth.UserBean;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("local")
public class UserServiceTest {

	@MockBean private UserRepository userRepository;
	@MockBean private OrganizationRepository organizationRepository;
	@MockBean private OrganizationUserMapRepository organizationUserMapRepository;
	@MockBean private SshKeyRepository sshKeyRepository;
	@MockBean private PasswordEncoder passwordEncoder;
	
	@Autowired private UserService userService;
	
	private UserBean user;
	
	@BeforeEach
	public void setUp() throws Exception {

		given(passwordEncoder.encode("password")).willReturn("wordpass");
		given(passwordEncoder.matches("password", "wordpass")).willReturn(true);
		
		user = new UserBean();
		user.setUsername("username");
		user.setPasswordHash(passwordEncoder.encode("password"));
		user.setDisplayName("User Name");
		user.setEmail("username@email.com");
		List<UserBean> users = new ArrayList<UserBean>();
		users.add(user);
		
		given(userRepository.findByUsername("username")).willReturn(users);
	}
	
	@Test
	public void testGetUserByUsernameWithValidUsername() throws IOException {
		assertEquals(userService.getUserByUsername("username"), user);
	}
	
	@Test
	public void testGetUserByUsernameWithInValidUsername() throws IOException {
		assertThrows(UserNotExistException.class, () -> {
			userService.getUserByUsername("not_exist");
		});
	}
	
	@Test
	@WithMockUser(username="username")
	public void testUpdateUserWithAuthorizedUser() throws IOException {
		
		UserProfileDTO profile = new UserProfileDTO();
		profile.setUsername("username");
		profile.setDisplayName("Updated User Name");
		profile.setEmail("updated_username@email.com");
		
		userService.updateUser(profile);
		
		/*
		 * `user` is updated, but that's the same pointer.
		 */
		verify(userRepository, times(1)).saveAndFlush(user);
		assertEquals(user.getDisplayName(), "Updated User Name");
		assertEquals(user.getEmail(), "updated_username@email.com");
	}
	
	@Test
	@WithMockUser(username="hijacked_username")
	public void testUpdateUserWithHijackedUser() throws IOException {
		
		UserProfileDTO profile = new UserProfileDTO();
		profile.setUsername("username");
		profile.setDisplayName("Updated User Name");
		profile.setEmail("updated_username@email.com");
		
		assertThrows(AccessDeniedException.class, () -> {
			userService.updateUser(profile);
		});
	}
	
	@Test
	@WithMockUser(username="username")
	public void testUpdatePasswordCorrectOldPassword() throws IOException {
		
		UserRegisterDTO register = new UserRegisterDTO();
		register.setUsername("username");
		register.setPassword("new_password");
		
		assertTrue(userService.updatePassword(register, "password"));
		
		verify(userRepository, times(1)).saveAndFlush(user);
	}
	
	@Test
	@WithMockUser(username="username")
	public void testUpdatePasswordWrongOldPassword() throws IOException {
		
		UserRegisterDTO register = new UserRegisterDTO();
		register.setUsername("username");
		register.setPassword("new_password");
		
		assertFalse(userService.updatePassword(register, "wrong_password"));
		verify(userRepository, times(0)).saveAndFlush(user);
	}
}
