package enterovirus.protease.database;

import java.io.IOException;

import enterovirus.protease.domain.MemberBean;

public interface MemberRepository {

	public MemberBean findById(Integer id) throws IOException;
	public MemberBean findByUsername(String username) throws IOException;
	
	public MemberBean saveAndFlush(MemberBean member);
}
