package enterovirus.capsid.service;

import java.io.IOException;

import enterovirus.capsid.dto.MemberDTO;
import enterovirus.protease.domain.MemberBean;

public interface MemberService {

	public MemberDTO findDTOById (Integer id) throws IOException;
	public MemberDTO findDTOByUsername (String username) throws IOException;
	
	public MemberBean saveAndFlushFromDTO(MemberDTO memberDTO);
}
