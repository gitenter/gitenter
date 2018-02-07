package enterovirus.capsid.service;

import enterovirus.capsid.dto.MemberDTO;
import enterovirus.protease.domain.MemberBean;

public interface MemberService {

	public MemberBean registerNewUserAccount(MemberDTO memberDTO); //throws EmailExistsException;
}
