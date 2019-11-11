package com.gitenter.capsid.api.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
	public void registerAndObtainAccessToken() throws Exception {
		String clientId = "gitenter-envuelope";
		String clientSecret = "secretpassword";
		
		String username = RandomStringUtils.random(10, true, false);
		String password = "password";
		
		registerUser(username, password);
		String tokenValue = obtainAccessToken(clientId, clientSecret, username, password);
		System.out.println(tokenValue);
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
 
	private void registerUser(String username, String password) throws JSONException {
		JSONObject requestParams = new JSONObject();
		requestParams.put("username", username);
		requestParams.put("password", password);
		requestParams.put("displayName", username);
		requestParams.put("email", username+"@company.com");
		
		Response response = RestAssured.given()
			.header("Content-type", "application/json")
			.body(requestParams.toString())
			.post("/api/register");
		
		assertEquals(response.getStatusCode(), 200);
	}
	
	private String obtainAccessToken(
		String clientId, String clientSecret, String username, String password) {
	
		Map<String, String> params = new HashMap<>();
		params.put("grant_type", "password");
		params.put("username", username);
		params.put("password", password);
		Response response = RestAssured.given()
			.auth().preemptive().basic(clientId, clientSecret)
			.and().with().params(params).when()
			.post("http://localhost:8080/oauth/token");
		return response.jsonPath().getString("access_token");
	}
}