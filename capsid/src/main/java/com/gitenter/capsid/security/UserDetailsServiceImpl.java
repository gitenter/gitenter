package com.gitenter.capsid.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.gitenter.protease.dao.auth.PersonRepository;
import com.gitenter.protease.domain.auth.PersonBean;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired private PersonRepository personRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		List<PersonBean> persons = personRepository.findByUsername(username);
		if (persons.size() == 0) {
			throw new UsernameNotFoundException(username);
		}
		PersonBean person = persons.get(0);
		
		return new GitEnterUser(person.getUsername(), person.getPassword());
	}
}
