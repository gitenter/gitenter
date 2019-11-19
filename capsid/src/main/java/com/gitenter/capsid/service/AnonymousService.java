package com.gitenter.capsid.service;

import java.io.IOException;

import com.gitenter.capsid.dto.PersonRegisterDTO;

public interface AnonymousService {
	
	public void signUp(PersonRegisterDTO signUpDTO) throws IOException;

//	public SignupDTO findDTOById (Integer id) throws IOException;
//	public SignupDTO findDTOByUsername (String username) throws IOException;
//	
//	public PersonBean saveAndFlushFromDTO(SignupDTO personDTO);
}
