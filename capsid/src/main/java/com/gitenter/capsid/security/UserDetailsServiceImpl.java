package com.gitenter.capsid.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.gitenter.protease.dao.auth.UserRepository;
import com.gitenter.protease.domain.auth.UserBean;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

	/*
	 * Cannot autowire `userService`, as it will (1) cause circular reference
	 * and (2) raises a different exception if username does not exist.
	 */
	@Autowired private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		List<UserBean> users = userRepository.findByUsername(username);
		if (users.size() == 0) {
			throw new UsernameNotFoundException(username);
		}
		UserBean user = users.get(0);
		
		return new GitEnterUserDetails(user.getUsername(), user.getPasswordHash());
	}
}
