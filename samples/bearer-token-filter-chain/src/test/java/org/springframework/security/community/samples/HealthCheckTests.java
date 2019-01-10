package org.springframework.security.community.samples;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("perform health check")
public class HealthCheckTests {

	@Autowired
	private MockMvc mvc;

	@SpyBean
	public UserDetailsService userDetailsService;

	private User.UserBuilder user = User.withDefaultPasswordEncoder()
		.username("user")
		.password("password")
		.roles("USER");

	@BeforeEach
	public void returnValidUser() {
		doReturn(user.build())
			.when(userDetailsService).loadUserByUsername(any(String.class));
	}

	@Test
	@DisplayName("pages are secured")
	void pageIsSecure() throws Exception {
		mvc.perform(
			MockMvcRequestBuilders.get("/secure")
		)
			.andExpect(status().is4xxClientError())
			.andExpect(unauthenticated())
		;
	}

	@Test
	@DisplayName("form login works")
	void doLogin() throws Exception {
		mvc.perform(
			MockMvcRequestBuilders.post("/login")
				.param("username", "user")
				.param("password", "password")
		)
			.andExpect(status().is3xxRedirection())
			.andExpect(authenticated())
		;

	}

	@Test
	@DisplayName("http-basic login works")
	void httpBasic() throws Exception {
		mvc.perform(
			MockMvcRequestBuilders.get("/secure")
				.header("Authorization", "Basic " + Base64.encodeBase64String("user:password".getBytes()))
		)
			.andExpect(authenticated())
		;
	}

	@Test
	@DisplayName("health check")
	void healthCheck() throws Exception {
		mvc.perform(
			MockMvcRequestBuilders.get("/healthcheck")
		)
			.andExpect(status().isOk())
			.andExpect(unauthenticated())
			.andExpect(content().string("OK"))
		;
	}
}
