package com.gitenter.capsid.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/*
 * TODO:
 * This hack (together with `.antMatchers(HttpMethod.OPTIONS, "/oauth/token").permitAll()`
 * in `WebSecurityConfig` is to resolve the problem that 
 * `curl -vs -X OPTIONS http://localhost:8080/oauth/token` returns 401.
 * https://github.com/spring-projects/spring-security-oauth/issues/938
 * https://stackoverflow.com/a/30638914/2467072
 * 
 * Very unfortunate, `/oauth/token` cannot be covered by `http.cors();` 
 * (in `ResourceServerConfig`) which gives OPTIONS preflights 200 
 * without a need of a credential, and works for a general endpoint.
 * 
 * As a side effect, it actually changes the headers.
 * 
 * Before:
 * $ curl -vs -X OPTIONS http://localhost:8080/api/users
 * ...
< HTTP/1.1 200
< Vary: Origin
< Vary: Access-Control-Request-Method
< Vary: Access-Control-Request-Headers
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 1; mode=block
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Expires: 0
< X-Frame-Options: DENY
< Content-Length: 0
< Date: Fri, 20 Dec 2019 22:54:33 GMT
 * 
 * After:
 * $ curl -vs -X OPTIONS http://localhost:8080/api/users
 * ...
< HTTP/1.1 200
< Access-Control-Allow-Origin: *
< Access-Control-Allow-Methods: POST, GET, OPTIONS, DELETE
< Access-Control-Max-Age: 3600
< Access-Control-Allow-Headers: x-requested-with, authorization, cache-control
< Content-Length: 0
< Date: Fri, 20 Dec 2019 23:05:01 GMT
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class SimpleCorsFilter implements Filter {

	public SimpleCorsFilter() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) res;
		HttpServletRequest request = (HttpServletRequest) req;
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", "x-requested-with, authorization, cache-control, content-type");
		response.setHeader("Access-Control-Allow-Credentials", "true");

		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			chain.doFilter(req, res);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) {
	}

	@Override
	public void destroy() {
	}
}