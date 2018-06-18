package enterovirus.capsid.service;

import java.io.IOException;

import com.gitenter.domain.settings.MemberBean;

import enterovirus.capsid.dto.MemberDTO;

public interface MemberService {

	public MemberDTO findDTOById (Integer id) throws IOException;
	public MemberDTO findDTOByUsername (String username) throws IOException;
	
	public MemberBean saveAndFlushFromDTO(MemberDTO memberDTO);
}
