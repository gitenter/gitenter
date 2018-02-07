package enterovirus.capsid.service;

import enterovirus.capsid.dto.MemberDTO;
import enterovirus.protease.domain.MemberBean;

public interface MemberService {

	public MemberBean registerNewMember(MemberDTO memberDTO); //throws EmailExistsException;
}
