package com.gitenter.capsid.service;

import java.io.IOException;

import com.gitenter.capsid.dto.UserRegisterDTO;
import com.gitenter.protease.domain.auth.UserBean;

public interface AnonymousService {
	
	public UserBean signUp(UserRegisterDTO signUpDTO) throws IOException;

//	public SignupDTO findDTOById (Integer id) throws IOException;
//	public SignupDTO findDTOByUsername (String username) throws IOException;
//	
//	public UserBean saveAndFlushFromDTO(SignupDTO userDTO);
}
