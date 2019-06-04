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

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MvcVariablesTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Test
	public void testNotMatchUrlToUsername() throws Exception {
		runTest("/test/test/anything", "admin");
	}

	@Test
	public void testMatchUrlToUsername() throws Exception {
		runTest("/test/admin/anything", "admin");
	}

	private void runTest(String url, String username) throws Exception {
		mvc.perform(
			get(url)
				.with(authentication(
					new UsernamePasswordAuthenticationToken(
						username,
						null,
						Collections.singletonList(new SimpleGrantedAuthority("ADMIN"))
					)
				))

		)
			.andExpect(authenticated())
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("You are authenticated:admin")))
		;
	}

}
