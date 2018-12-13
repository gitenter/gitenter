package com.gitenter.capsid.service;

import com.gitenter.capsid.dto.MemberRegisterDTO;

public interface AnonymousService {
	
	public void signUp(MemberRegisterDTO signUpDTO);

//	public SignupDTO findDTOById (Integer id) throws IOException;
//	public SignupDTO findDTOByUsername (String username) throws IOException;
//	
//	public MemberBean saveAndFlushFromDTO(SignupDTO memberDTO);
}
