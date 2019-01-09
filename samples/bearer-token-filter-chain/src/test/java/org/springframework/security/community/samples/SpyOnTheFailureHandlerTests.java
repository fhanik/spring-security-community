package org.springframework.security.community.samples;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("we can spy on failure handler")
public class SpyOnTheFailureHandlerTests {

	@Autowired
	private MockMvc mvc;

	@SpyBean
	AuthenticationFailureHandler failureHandler;

	@SpyBean
	public UserDetailsService userDetailsService;

	private User.UserBuilder user = User.withDefaultPasswordEncoder()
		.username("user")
		.password("password")
		.roles("USER");

	@Test
	@DisplayName("account expired throws AccountExpiredException")
	void accountExpired() throws Exception {
		doReturn(user
			.username("expired")
			.accountExpired(true)
			.build()
		).when(userDetailsService).loadUserByUsername(any(String.class));
		mvc.perform(
			MockMvcRequestBuilders.post("/login")
				.param("username", "expired")
				.param("password", "password")
		)
			.andExpect(status().is4xxClientError())
			.andExpect(unauthenticated())
		;
		Mockito.verify(failureHandler).onAuthenticationFailure(
			any(),
			any(),
			any(AccountExpiredException.class)
		);
	}

	@Test
	@DisplayName("account expired throws LockedException")
	void accountLocked() throws Exception {
		doReturn(user
			.username("locked")
			.accountLocked(true)
			.build()
		).when(userDetailsService).loadUserByUsername(any(String.class));
		mvc.perform(
			MockMvcRequestBuilders.post("/login")
				.param("username", "locked")
				.param("password", "password")
		)
			.andExpect(status().is4xxClientError())
			.andExpect(unauthenticated())
		;
		Mockito.verify(failureHandler).onAuthenticationFailure(
			any(),
			any(),
			any(LockedException.class)
		);
	}

	@Test
	@DisplayName("form login works")
	void happyPath() throws Exception {
		doReturn(user.build())
			.when(userDetailsService).loadUserByUsername(any(String.class));
		mvc.perform(
			MockMvcRequestBuilders.post("/login")
				.param("username", "user")
				.param("password", "password")
		)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"))
			.andExpect(authenticated())
		;
	}

}
