package com.gitenter.envelope.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gitenter.protease.dao.auth.MemberRepository;
import com.gitenter.protease.domain.auth.MemberBean;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired private MemberRepository memberRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		List<MemberBean> members = memberRepository.findByUsername(username);
		if (members.size() == 0) {
			throw new UsernameNotFoundException(username);
		}
		MemberBean member = members.get(0);
			
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
			
		return new User(member.getUsername(), member.getPassword(), authorities);
	}
}
