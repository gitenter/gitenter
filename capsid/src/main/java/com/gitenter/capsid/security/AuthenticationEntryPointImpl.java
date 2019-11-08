package com.gitenter.capsid.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

	private final HttpMessageConverter<String> messageConverter;

	private final ObjectMapper mapper;

	public AuthenticationEntryPointImpl(ObjectMapper mapper) {
		 this.messageConverter = new StringHttpMessageConverter();
		 this.mapper = mapper;
	}

	@Override
	public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {

		/*
		 * TODO:
		 * Complete it:
		 * https://github.com/sermore/spring-glee-o-meter/blob/master/src/main/java/net/reliqs/gleeometer/errors/ApiError.java
		 */
		
//		 ApiError apiError = new ApiError(UNAUTHORIZED);
//		 apiError.setMessage(e.getMessage());
//		 apiError.setDebugMessage(e.getMessage());
//
//		 ServerHttpResponse outputMessage = new ServletServerHttpResponse(httpServletResponse);
//		 outputMessage.setStatusCode(HttpStatus.UNAUTHORIZED);
//
//		 messageConverter.write(mapper.writeValueAsString(apiError), MediaType.APPLICATION_JSON, outputMessage);
	}
}
