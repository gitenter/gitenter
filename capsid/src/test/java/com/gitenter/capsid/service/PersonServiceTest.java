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

import com.gitenter.capsid.dto.PersonProfileDTO;
import com.gitenter.capsid.dto.PersonRegisterDTO;
import com.gitenter.capsid.service.exception.UserNotExistException;
import com.gitenter.protease.dao.auth.PersonRepository;
import com.gitenter.protease.dao.auth.OrganizationPersonMapRepository;
import com.gitenter.protease.dao.auth.OrganizationRepository;
import com.gitenter.protease.dao.auth.SshKeyRepository;
import com.gitenter.protease.domain.auth.PersonBean;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("local")
public class PersonServiceTest {

	@MockBean private PersonRepository personRepository;
	@MockBean private OrganizationRepository organizationRepository;
	@MockBean private OrganizationPersonMapRepository organizationMemberMapRepository;
	@MockBean private SshKeyRepository sshKeyRepository;
	@MockBean private PasswordEncoder passwordEncoder;
	
	@Autowired private PersonService personService;
	
	private PersonBean person;
	
	@BeforeEach
	public void setUp() throws Exception {

		given(passwordEncoder.encode("password")).willReturn("wordpass");
		given(passwordEncoder.matches("password", "wordpass")).willReturn(true);
		
		person = new PersonBean();
		person.setUsername("username");
		person.setPassword(passwordEncoder.encode("password"));
		person.setDisplayName("User Name");
		person.setEmail("username@email.com");
		List<PersonBean> persons = new ArrayList<PersonBean>();
		persons.add(person);
		
		given(personRepository.findByUsername("username")).willReturn(persons);
	}
	
	@Test
	public void testGetPersonByUsernameWithValidUsername() throws IOException {
		assertEquals(personService.getPersonByUsername("username"), person);
	}
	
	@Test
	public void testGetPersonByUsernameWithInValidUsername() throws IOException {
		assertThrows(UserNotExistException.class, () -> {
			personService.getPersonByUsername("not_exist");
		});
	}
	
	@Test
	@WithMockUser(username="username")
	public void testUpdatePersonWithAuthorizedUser() throws IOException {
		
		PersonProfileDTO profile = new PersonProfileDTO();
		profile.setUsername("username");
		profile.setDisplayName("Updated User Name");
		profile.setEmail("updated_username@email.com");
		
		personService.updatePerson(profile);
		
		/*
		 * `member` is updated, but that's the same pointer.
		 */
		verify(personRepository, times(1)).saveAndFlush(person);
		assertEquals(person.getDisplayName(), "Updated User Name");
		assertEquals(person.getEmail(), "updated_username@email.com");
	}
	
	@Test
	@WithMockUser(username="hijacked_username")
	public void testUpdatePersonWithHijackedUser() throws IOException {
		
		PersonProfileDTO profile = new PersonProfileDTO();
		profile.setUsername("username");
		profile.setDisplayName("Updated User Name");
		profile.setEmail("updated_username@email.com");
		
		assertThrows(AccessDeniedException.class, () -> {
			personService.updatePerson(profile);
		});
	}
	
	@Test
	@WithMockUser(username="username")
	public void testUpdatePasswordCorrectOldPassword() throws IOException {
		
		PersonRegisterDTO register = new PersonRegisterDTO();
		register.setUsername("username");
		register.setPassword("new_password");
		
		assertTrue(personService.updatePassword(register, "password"));
		
		verify(personRepository, times(1)).saveAndFlush(person);
	}
	
	@Test
	@WithMockUser(username="username")
	public void testUpdatePasswordWrongOldPassword() throws IOException {
		
		PersonRegisterDTO register = new PersonRegisterDTO();
		register.setUsername("username");
		register.setPassword("new_password");
		
		assertFalse(personService.updatePassword(register, "wrong_password"));
		verify(personRepository, times(0)).saveAndFlush(person);
	}
}
