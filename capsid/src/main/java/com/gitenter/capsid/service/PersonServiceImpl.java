package com.gitenter.capsid.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gitenter.capsid.dto.PersonProfileDTO;
import com.gitenter.capsid.dto.PersonRegisterDTO;
import com.gitenter.capsid.service.exception.UserNotExistException;
import com.gitenter.protease.dao.auth.PersonRepository;
import com.gitenter.protease.dao.auth.SshKeyRepository;
import com.gitenter.protease.domain.auth.PersonBean;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.OrganizationPersonRole;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.auth.RepositoryPersonRole;
import com.gitenter.protease.domain.auth.SshKeyBean;

/*
 * It is quite ironical that Spring @autowired are contradict with
 * object-oriented programming. Say a more OO approach is we have
 * a domain class "Person" which:
 * (1) hold its own information such as its "username", and
 * (2) can "createOrganization()" so need to @autowired "*Repository" in.
 * But it seems impossible in the current framework.
 * 
 * Therefore, these classes will go with really procedured approach. 
 */
@Service
public class PersonServiceImpl implements PersonService {
	
	private static final Logger auditLogger = LoggerFactory.getLogger("audit");

	private final PersonRepository personRepository;
	private final SshKeyRepository sshKeyRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	@Autowired
	public PersonServiceImpl(
			PersonRepository personRepository, 
			SshKeyRepository sshKeyRepository,
			PasswordEncoder passwordEncoder) {
		this.personRepository = personRepository;
		this.sshKeyRepository = sshKeyRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public PersonBean getPersonByUsername(String username) throws IOException {
		List<PersonBean> persons = personRepository.findByUsername(username);
		if (persons.size() == 1) {
			return persons.get(0);
		}
		else {
			throw new UserNotExistException(username);
		}
	}
	
	@Override
	@PreAuthorize("isAuthenticated()")
	public PersonBean getMe(Authentication authentication) throws IOException {
		return getPersonByUsername(authentication.getName());
	}
	
	@Override
	public PersonProfileDTO getPersonProfileDTO(Authentication authentication) throws IOException {
		
		PersonBean person = getPersonByUsername(authentication.getName());
		
		PersonProfileDTO profile = new PersonProfileDTO();
		profile.fillFromBean(person);
		
		return profile;
	}
	
	@Override
	public PersonRegisterDTO getPersonRegisterDTO(Authentication authentication) throws IOException {
		
		PersonBean person = getPersonByUsername(authentication.getName());
		
		/*
		 * Can just use superclass method, as fulfill "password"
		 * attribute is not necessary.
		 */
		PersonRegisterDTO profileAndPassword = new PersonRegisterDTO();
		profileAndPassword.fillFromBean(person);
		
		return profileAndPassword;
	}
	
	@Override
	@PreAuthorize("hasPermission(#profile, T(com.gitenter.capsid.security.PersonSecurityRole).SELF)")
	public void updatePerson(PersonProfileDTO profile) throws IOException {
		
		PersonBean person = getPersonByUsername(profile.getUsername());
		profile.updateBean(person);
		
		/* Since "saveAndFlush()" will decide by itself whether the operation is
		 * INSERT or UPDATE, the bean being actually modified and refreshed should 
		 * be the bean queried from the database, rather than the bean user just
		 * produced. 
		 */
		personRepository.saveAndFlush(person);
	}
	
	@Override
	@PreAuthorize("hasPermission(#register, T(com.gitenter.capsid.security.PersonSecurityRole).SELF)")
	public boolean updatePassword(PersonRegisterDTO register, String oldPassword) throws IOException {
		
		PersonBean person = getPersonByUsername(register.getUsername());
		
		if (!passwordEncoder.matches(oldPassword, person.getPassword())) {
			return false;
		}
		else {
			person.setPassword(passwordEncoder.encode(register.getPassword()));
			personRepository.saveAndFlush(person);
			
			return true;
		}
	}
	
	/*
	 * No need for authorization, because users are accessible to other user's
	 * managed/belonged organizations and repositories.
	 */
	@Override
	public Collection<OrganizationBean> getManagedOrganizations(String username) throws IOException {
		
		/* 
		 * I believe that Hibernate should be smart enough that when
		 * "personRepository.findById()" is called the second time in an execution,
		 * it will not reach the database again but use the same return value.
		 * (As it march with what Martin Flower said of "Identity Map" of an ORM
		 * design). Need to double check. 
		 * 
		 * If not, we need to find out a smarter way to handle the case of listing 
		 * all organizations (we can have it iterated/filtered multiple times 
		 * when display), for example a dirty fix of implement a proxy pattern
		 * inside of the "getMember()" method
		 */
		PersonBean person = getPersonByUsername(username);
		return person.getOrganizations(OrganizationPersonRole.MANAGER);
	}

	@Override
	public Collection<OrganizationBean> getBelongedOrganizations(String username) throws IOException {
		
		PersonBean person = getPersonByUsername(username);
		return person.getOrganizations(OrganizationPersonRole.MEMBER);
	}

	@Override
	public Collection<RepositoryBean> getOrganizedRepositories(String username) throws IOException {
		
		PersonBean person = getPersonByUsername(username);
		return person.getRepositories(RepositoryPersonRole.ORGANIZER);
	}

	@Override
	public Collection<RepositoryBean> getAuthoredRepositories(String username) throws IOException {
		
		PersonBean person = getPersonByUsername(username);
		return person.getRepositories(RepositoryPersonRole.EDITOR);
	}
	
	@Override
	@PreAuthorize("hasPermission(#person, T(com.gitenter.capsid.security.PersonSecurityRole).SELF)")
	public void addSshKey(SshKeyBean sshKey, PersonBean person) throws IOException {
		
		sshKey.setPerson(person);
		person.addSshKey(sshKey);
		
		sshKeyRepository.saveAndFlush(sshKey);
	}

	@Override
	@PreAuthorize("hasPermission(#username, T(com.gitenter.capsid.security.PersonSecurityRole).SELF)")
	public boolean deletePerson(String username, String password) throws IOException {
		
		PersonBean person = getPersonByUsername(username);
		
		if (!passwordEncoder.matches(password, person.getPassword())) {
			return false;
		}
		else {
			auditLogger.info("User account has been deleted: "+person);
			personRepository.delete(person);
			return true;
		}
	}
}
