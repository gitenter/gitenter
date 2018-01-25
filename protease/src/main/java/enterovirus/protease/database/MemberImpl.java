package enterovirus.protease.database;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import enterovirus.protease.domain.MemberBean;

@Repository
public class MemberImpl implements MemberRepository {

	@Autowired private MemberDatabaseRepository memberDbRepository;
	
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
