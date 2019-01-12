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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("testing Spring Security password encoder ")
class InMemoryAuthTests {

	@Autowired
	private MockMvc mvc;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Test
	@DisplayName("user / 123 authentication")
	void user() throws Exception {
		mvc.perform(
			post("/login")
				.param("username", "admin")
				.param("password", "password")
				.with(csrf())
		)
			.andExpect(authenticated())
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"))
		;
	}

	@Test
	@DisplayName("admin / password authentication")
	void admin() throws Exception {
		mvc.perform(
			post("/login")
				.param("username", "admin")
				.param("password", "password")
				.with(csrf())
		)
			.andExpect(authenticated())
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"))
		;
	}

	@Test
	@DisplayName("authentication fails")
	void fails() throws Exception {
		mvc.perform(
			post("/login")
				.param("username", "admin")
				.param("password", "invalid-password")
				.with(csrf())
		)
			.andExpect(unauthenticated())
			.andExpect(redirectedUrl("/login?error"))
		;
	}

	@Test
	void printPasswords() {
		System.out.println("123 = "+passwordEncoder.encode("123"));
		System.out.println("password = "+passwordEncoder.encode("password"));
	}

}
