package com.gitenter.database.auth;

import java.io.IOException;

import com.gitenter.domain.auth.MemberBean;

public interface MemberRepository {

	public MemberBean findById(Integer id) throws IOException;
	public MemberBean findByUsername(String username) throws IOException;
	
	public MemberBean saveAndFlush(MemberBean member);
}
