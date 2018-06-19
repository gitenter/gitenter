package com.gitenter.database.auth;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gitenter.domain.auth.MemberBean;

@Repository
public class MemberImpl implements MemberRepository {

	@Autowired private MemberDatabaseRepository memberDbRepository;
	
	/*
	 * TODO:
	 * Consider throw other exception better than "IOException".
	 * 
	 * (1) Maybe something in "org.springframework.beans" such as
	 * "BeanCreationException" or "NoSuchBeanDefinitionException".
	 * But those exceptions seems not design for that.
	 * 
	 * (2) Or to define own classes such as "MemberNotFoundException".
	 * However, the way to use it is together with "@ResponseStatus(value=HttpStatus.NOT_FOUND)",
	 * but "@ResponseStatus" is a Spring Web annotation, and here is
	 * just about the persistent layer, so it is weird to have it
	 * define in here.
	 */
	public MemberBean findById(Integer id) throws IOException {
		
		Optional<MemberBean> members = memberDbRepository.findById(id);
		if (!members.isPresent()) {
			throw new IOException ("Member id "+id+" does not exist!");
		}
		return members.get();
	}
	
	public MemberBean findByUsername(String username) throws IOException {
		
		List<MemberBean> members = memberDbRepository.findByUsername(username);
		if (members.size() > 1) {
			throw new IOException ("username "+username+" is not unique!");
		}
		else if (members.size() == 0) {
			throw new IOException ("username "+username+" does not exist!");
		}
		
		return members.get(0);
	}

	public MemberBean saveAndFlush(MemberBean member) {
		return memberDbRepository.saveAndFlush(member);
	}
}
