package com.gitenter.database.settings;

import java.io.IOException;

import com.gitenter.domain.settings.MemberBean;

public interface MemberRepository {

	public MemberBean findById(Integer id) throws IOException;
	public MemberBean findByUsername(String username) throws IOException;
	
	public MemberBean saveAndFlush(MemberBean member);
}
