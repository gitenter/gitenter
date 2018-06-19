package enterovirus.capsid.service;

import java.io.IOException;
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

import com.gitenter.dao.auth.MemberRepository;
import com.gitenter.domain.auth.MemberBean;

import enterovirus.protease.database.*;
import enterovirus.protease.domain.*;

@Service
public class MemberUserDetailsService implements UserDetailsService {

	@Autowired private MemberRepository memberRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		try {
			MemberBean member = memberRepository.findByUsername(username);
			
			List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
			authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
			
			return new User(
					member.getUsername(),
					member.getPassword(),
					authorities);
		}
		catch (IOException e) {
			throw new UsernameNotFoundException(e.getMessage());
		}
	}
}
