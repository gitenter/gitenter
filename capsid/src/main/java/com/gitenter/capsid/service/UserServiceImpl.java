package com.gitenter.capsid.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gitenter.capsid.dto.ChangePasswordDTO;
import com.gitenter.capsid.dto.UserProfileDTO;
import com.gitenter.capsid.dto.UserRegisterDTO;
import com.gitenter.capsid.service.exception.UserNotExistException;
import com.gitenter.protease.dao.auth.SshKeyRepository;
import com.gitenter.protease.dao.auth.UserRepository;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.OrganizationUserRole;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.auth.RepositoryUserRole;
import com.gitenter.protease.domain.auth.SshKeyBean;
import com.gitenter.protease.domain.auth.UserBean;

/*
 * It is quite ironical that Spring @autowired are contradict with
 * object-oriented programming. Say a more OO approach is we have
 * a domain class "User" which:
 * (1) hold its own information such as its "username", and
 * (2) can "createOrganization()" so need to @autowired "*Repository" in.
 * But it seems impossible in the current framework.
 * 
 * Therefore, these classes will go with really procedured approach. 
 */
@Service
public class UserServiceImpl implements UserService {
	
	private static final Logger auditLogger = LoggerFactory.getLogger("audit");

	private final UserRepository userRepository;
	private final SshKeyRepository sshKeyRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	@Autowired
	public UserServiceImpl(
			UserRepository userRepository, 
			SshKeyRepository sshKeyRepository,
			PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.sshKeyRepository = sshKeyRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Override
	public UserBean getUserById(Integer memberId) throws UserNotExistException {
		Optional<UserBean> members = userRepository.findById(memberId);
		if (members.isPresent()) {
			return members.get();
		}
		else {
			throw new UserNotExistException(memberId);
		}
	}

	@Override
	public UserBean getUserByUsername(String username) throws UserNotExistException {
		List<UserBean> users = userRepository.findByUsername(username);
		if (users.size() == 1) {
			return users.get(0);
		}
		else {
			throw new UserNotExistException(username);
		}
	}
	
	/*
	 * Maybe no need to pass `Authentication` from controller layer. But maybe we need
	 * to hack the system a little bit to achieve that.
	 * https://www.mkyong.com/spring-security/get-current-logged-in-username-in-spring-security/
	 * https://stackoverflow.com/a/36936819/2467072
	 * https://stackoverflow.com/a/31050615/2467072
	 */
	@Override
	@PreAuthorize("isAuthenticated()")
	public UserBean getMe(Authentication authentication) throws UserNotExistException {
		return getUserByUsername(authentication.getName());
	}
	
	@Override
	public UserProfileDTO getUserProfileDTO(Authentication authentication) throws IOException {
		
		UserBean user = getUserByUsername(authentication.getName());
		
		UserProfileDTO profile = new UserProfileDTO();
		profile.fillFromBean(user);
		
		return profile;
	}
	
	@Override
	public UserRegisterDTO getUserRegisterDTO(Authentication authentication) throws IOException {
		
		UserBean user = getUserByUsername(authentication.getName());
		
		/*
		 * Can just use superclass method, as fulfill "password"
		 * attribute is not necessary.
		 */
		UserRegisterDTO profileAndPassword = new UserRegisterDTO();
		profileAndPassword.fillFromBean(user);
		
		return profileAndPassword;
	}
	
	@Override
	@PreAuthorize("hasPermission(#profile, T(com.gitenter.capsid.security.UserSecurityRole).SELF)")
	public UserBean updateUser(UserProfileDTO profile) throws IOException {
		
		UserBean user = getUserByUsername(profile.getUsername());
		profile.updateBean(user);
		
		/* Since "saveAndFlush()" will decide by itself whether the operation is
		 * INSERT or UPDATE, the bean being actually modified and refreshed should 
		 * be the bean queried from the database, rather than the bean user just
		 * produced. 
		 */
		return userRepository.saveAndFlush(user);
	}
	
	@Override
	@PreAuthorize("isAuthenticated()")
	public boolean updatePassword(Authentication authentication, ChangePasswordDTO changePasswordDTO) throws IOException {
		
		UserBean me = getMe(authentication);
		if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), me.getPasswordHash())) {
			return false;
		}
		else {
			me.setPasswordHash(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
			userRepository.saveAndFlush(me);
			
			return true;
		}
	}
	
	/*
	 * TODO:
	 * Deprecated after removing web controller.
	 */
	@Override
	@PreAuthorize("hasPermission(#register, T(com.gitenter.capsid.security.UserSecurityRole).SELF)")
	public boolean updatePassword(UserRegisterDTO register, String oldPassword) throws IOException {
		
		UserBean user = getUserByUsername(register.getUsername());
		
		if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
			return false;
		}
		else {
			user.setPasswordHash(passwordEncoder.encode(register.getPassword()));
			userRepository.saveAndFlush(user);
			
			return true;
		}
	}
	
	/*
	 * No need for authorization, because users are accessible to other user's
	 * managed/belonged organizations and repositories.
	 */
	@Override
	public List<OrganizationBean> getManagedOrganizations(String username) throws IOException {
		
		/* 
		 * I believe that Hibernate should be smart enough that when
		 * "userRepository.findById()" is called the second time in an execution,
		 * it will not reach the database again but use the same return value.
		 * (As it march with what Martin Flower said of "Identity Map" of an ORM
		 * design). Need to double check. 
		 * 
		 * If not, we need to find out a smarter way to handle the case of listing 
		 * all organizations (we can have it iterated/filtered multiple times 
		 * when display), for example a dirty fix of implement a proxy pattern
		 * inside of the "getUser()" method
		 */
		UserBean user = getUserByUsername(username);
		return user.getOrganizations(OrganizationUserRole.MANAGER);
	}

	@Override
	public List<OrganizationBean> getBelongedOrganizations(String username) throws IOException {
		
		UserBean user = getUserByUsername(username);
		return user.getOrganizations(OrganizationUserRole.ORDINARY_MEMBER);
	}

	@Override
	public List<RepositoryBean> getOrganizedRepositories(String username) throws IOException {
		
		UserBean user = getUserByUsername(username);
		return user.getRepositories(RepositoryUserRole.PROJECT_ORGANIZER);
	}

	@Override
	public List<RepositoryBean> getAuthoredRepositories(String username) throws IOException {
		
		UserBean user = getUserByUsername(username);
		return user.getRepositories(RepositoryUserRole.EDITOR);
	}
	
	@Override
	@PreAuthorize("hasPermission(#user, T(com.gitenter.capsid.security.UserSecurityRole).SELF)")
	public void addSshKey(SshKeyBean sshKey, UserBean user) throws IOException {
		
		sshKey.setUser(user);
		user.addSshKey(sshKey);
		
		sshKeyRepository.saveAndFlush(sshKey);
	}

	@Override
	@PreAuthorize("hasPermission(#username, T(com.gitenter.capsid.security.UserSecurityRole).SELF)")
	public boolean deleteUser(String username, String password) throws IOException {
		
		UserBean user = getUserByUsername(username);
		
		if (!passwordEncoder.matches(password, user.getPasswordHash())) {
			return false;
		}
		else {
			auditLogger.info("User account has been deleted: "+user);
			userRepository.delete(user);
			return true;
		}
	}
}
