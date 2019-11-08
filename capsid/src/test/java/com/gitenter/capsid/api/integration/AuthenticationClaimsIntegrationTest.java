package com.gitenter.capsid.api.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import com.gitenter.capsid.CapsidApplication;

import io.restassured.RestAssured;
import io.restassured.response.Response;

//@RunWith(SpringRunner.class)
//@SpringBootTest(
//	classes = CapsidApplication.class, 
//	webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
public class AuthenticationClaimsIntegrationTest {
	
	@Test
	public void pass() {
		
	}
 
//	@Autowired
//	private JwtTokenStore tokenStore;
// 
//	@Test
//	public void whenTokenDoesNotContainIssuer_thenSuccess() {
//		String tokenValue = obtainAccessToken("fooClientIdPassword", "john", "123");
//		OAuth2Authentication auth = tokenStore.readAuthentication(tokenValue);
//		Map<String, Object> details = (Map<String, Object>) auth.getDetails();
//	
//		assertTrue(details.containsKey("organization"));
//	}
// 
//	private String obtainAccessToken(
//		String clientId, String username, String password) {
//	
//		Map<String, String> params = new HashMap<>();
//		params.put("grant_type", "password");
//		params.put("client_id", clientId);
//		params.put("username", username);
//		params.put("password", password);
//		Response response = RestAssured.given()
//			.auth().preemptive().basic(clientId, "secret")
//			.and().with().params(params).when()
//			.post("http://localhost:8080/spring-security-oauth-server/oauth/token");
//		return response.jsonPath().getString("access_token");
//	}
}