/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.springframework.security.community.samples;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static java.util.Arrays.asList;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("testing 2 layers of rest authentication")
class RestAuthenticationTests {

	@Autowired
	private MockMvc mvc;

	@SpringBootConfiguration
	@EnableAutoConfiguration
	@Import({SecurityConfig.class, SampleAppController.class})
	public static class SpringBootApplicationTestConfig {
	}


	@Test
	@DisplayName("invocation works if API key is correct")
	void apiKeySuccess() throws Exception {
		mvc.perform(
			post("/api-key-only")
				.header("Authorization", "ApiKey this-is-a-valid-key")
				.with(SecurityMockMvcRequestPostProcessors.csrf())
		)
			.andExpect(status().isOk())
			.andExpect(authenticated()
				.withAuthorities(
					asList(
						new SimpleGrantedAuthority("API_KEY")
					)
				)
			)
			.andExpect(content().string("API KEY ONLY"))
		;
	}

	@Test
	@DisplayName("invocation works if API key is correct even with user credentials")
	void moreThanSufficientAuthentication() throws Exception {
		mvc.perform(
			post("/api-key-only")
				.header("Authorization", "ApiKey this-is-a-valid-key")
				.header("X-User-Credentials", "valid-user")
		)
			.andExpect(status().isOk())
			.andExpect(authenticated()
				.withAuthorities(
					asList(
						new SimpleGrantedAuthority("API_KEY"),
						new SimpleGrantedAuthority("USER_CREDENTIALS")
					)
				)
			)
			.andExpect(content().string("API KEY ONLY"))
		;
	}


	@Test
	@DisplayName("invocation fails because of an invalid key")
	void invalidKey() throws Exception {
		mvc.perform(
			post("/api-key-only")
				.header("Authorization", "ApiKey this-is-an-invalid-key")
		)
			.andExpect(status().is4xxClientError())
			.andExpect(unauthenticated())
		;
	}

	@Test
	@DisplayName("valid api key can't reach an endpoint with dual requirements")
	void insufficientAuthentication() throws Exception {
		mvc.perform(
			post("/dual-auth")
				.header("Authorization", "ApiKey this-is-a-valid-key")
		)
			.andExpect(status().is4xxClientError())
			.andExpect(authenticated()
				.withAuthorities(
					asList(
						new SimpleGrantedAuthority("API_KEY")
					)
				)
			)
		;
	}

	@Test
	@DisplayName("valid api key but invalid user auth")
	void insufficientAuthenticationForUser() throws Exception {
		mvc.perform(
			post("/dual-auth")
				.header("Authorization", "ApiKey this-is-a-valid-key")
				.header("X-User-Credentials", "invalid-user")
		)
			.andExpect(status().is4xxClientError())
			.andExpect(authenticated()
				.withAuthorities(
					asList(
						new SimpleGrantedAuthority("API_KEY")
					)
				)
			)
		;
	}

	@Test
	@DisplayName("valid api key but invalid user auth")
	void dualAuth() throws Exception {
		mvc.perform(
			post("/dual-auth")
				.header("Authorization", "ApiKey this-is-a-valid-key")
				.header("X-User-Credentials", "valid-user")
		)
			.andExpect(status().isOk())
			.andExpect(content().string("DUAL AUTH"))
			.andExpect(authenticated()
				.withAuthorities(
					asList(
						new SimpleGrantedAuthority("API_KEY"),
						new SimpleGrantedAuthority("USER_CREDENTIALS")
					)
				)
			)
			.andExpect(content().string("DUAL AUTH"))
		;
	}


}
