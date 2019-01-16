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

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("testing legacy Oauth 2 token enhancer")
class EnhanceTokenTests {

	@Autowired
	private MockMvc mvc;

	@Test
	@DisplayName("perform a password grant")
	void passwordGrant() throws Exception {
		getToken()
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("\"full_name\":\"Joe Schmoe\"")))
			.andExpect(content().string(containsString("\"email\":\"Joe@Schmoe.Com\"")))
		;
	}

	@Test
	void resourceServerRestCall() throws Exception {
		final String token = getToken(getToken().andReturn());
		mvc.perform(
			post("/hello")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
		)
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Hello to admin")))
		;

	}

	@Test
	void unauthorizedResourceServerRestCall() throws Exception {
		final String token = "invalid token";
		mvc.perform(
			post("/hello")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
		)
			.andExpect(status().isUnauthorized())
		;

	}

	private ResultActions getToken() throws Exception {
		return mvc.perform(
			post("/oauth/token")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.param("username", "admin")
				.param("password", "password")
				.param("grant_type", "password")
				.param("response_type", "token")
				.param("client_id", "testclient")
				.header("Authorization", "Basic " + Base64.encodeBase64String("testclient:secret".getBytes()))
		);
	}

	private String getToken(MvcResult result) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String content = result.getResponse().getContentAsString();
		final TypeReference<Map<String, Object>> type = new TypeReference<Map<String, Object>>() {
		};
		Map<String, Object> response = mapper.readValue(content, type);
		return (String) response.get("access_token");
	}
}
