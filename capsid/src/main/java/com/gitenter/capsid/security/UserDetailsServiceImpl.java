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

	@Autowired private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		List<UserBean> users = userRepository.findByUsername(username);
		if (users.size() == 0) {
			throw new UsernameNotFoundException(username);
		}
		UserBean user = users.get(0);
		
		return new GitEnterUser(user.getUsername(), user.getPassword());
	}
}
