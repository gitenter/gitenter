package com.gitenter.capsid.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.gitenter.protease.dao.auth.MemberRepository;
import com.gitenter.protease.domain.auth.MemberBean;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired private MemberRepository memberRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		List<MemberBean> members = memberRepository.findByUsername(username);
		if (members.size() == 0) {
			throw new UsernameNotFoundException(username);
		}
		MemberBean member = members.get(0);
		
		return new GitEnterUser(member.getUsername(), member.getPassword());
	}
}
