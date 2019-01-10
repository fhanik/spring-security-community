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

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.assertj.core.util.Arrays.array;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("testing Spring Security For a REST and Browser endpoint")
public class RestWithBrowserEndpointTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private JwtDecoder decoder;

	@BeforeEach
	public void mockDecoder() {
		Jwt jwt = new Jwt(
			"some-token",
			Instant.now(),
			Instant.now().plus(1, HOURS),
			singletonMap("kid",
				array("1")
			),
			singletonMap("scope", "USER")
		);
		Mockito.when(decoder.decode(any(String.class))).thenReturn(jwt);
	}

	private Authentication authentication = new UsernamePasswordAuthenticationToken(
		"user",
		"not needed",
		singletonList(new SimpleGrantedAuthority("ROLE_USER"))
	);

	@Test
	@DisplayName("authenticated browser request")
	public void htmlRequest() throws Exception {
		mvc.perform(
			get("/secure")
				.with(authentication(authentication))
		)
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("You are authenticated")))
		;
	}

	@Test
	@DisplayName("authenticated browser with mock user")
	@WithMockUser
	public void htmlRequestWithMockUser() throws Exception {
		mvc.perform(
			get("/secure")
		)
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("You are authenticated")))
		;
	}

	@Test
	@DisplayName("authenticated rest request")
	public void restRequest() throws Exception {
		mvc.perform(
			get("/secure")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.header(HttpHeaders.AUTHORIZATION, "Bearer yourJwtTokenOpaqueInTheFuture")
		)
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("OK")))
		;
	}


}
