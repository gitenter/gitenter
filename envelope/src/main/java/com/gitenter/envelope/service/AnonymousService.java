package com.gitenter.envelope.service;

import com.gitenter.envelope.dto.SignUpDTO;

public interface AnonymousService {
	
	public void signUp(SignUpDTO signUpDTO);

//	public SignupDTO findDTOById (Integer id) throws IOException;
//	public SignupDTO findDTOByUsername (String username) throws IOException;
//	
//	public MemberBean saveAndFlushFromDTO(SignupDTO memberDTO);
}
