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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("testing Spring Security Http/Https Redirects")
public class TestRequiresSecureRedirect {

	@Autowired
	private MockMvc mvc;

	private Authentication authentication = new UsernamePasswordAuthenticationToken(
		"user",
		"not needed",
		singletonList(new SimpleGrantedAuthority("ROLE_USER"))
	);

	@Test
	@DisplayName("redirects if request is received on port 8080")
	public void httpRequest() throws Exception {
		mvc.perform(
			get("/secure")
			.with(request -> {request.setServerPort(8080); return request;})
			.with(request -> {request.setSecure(false); return request;})
			.with(request -> {request.setScheme("http"); return request;})
		)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("https://localhost:8081/secure"))
		;
	}

	@Test
	@DisplayName("redirects to login page if using https")
	public void httpsRequestNoAuth() throws Exception {
		mvc.perform(
			get("/secure")
				.with(request -> {request.setServerPort(8081); return request;})
				.with(request -> {request.setSecure(true); return request;})
				.with(request -> {request.setScheme("https"); return request;})
		)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("https://localhost:8081/login"))
		;
	}

	@Test
	@DisplayName("works when authenticated over https")
	public void httpsAuthenticated() throws Exception {
		mvc.perform(
			get("/secure")
				.with(authentication(authentication))
				.with(request -> {request.setServerPort(8081); return request;})
				.with(request -> {request.setSecure(true); return request;})
				.with(request -> {request.setScheme("https"); return request;})
		)
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("You are authenticated")))
		;
	}

	@Test
	@DisplayName("what happens when request comes in over SSL but is not marked secure")
	public void whatHappens1() throws Exception {
		mvc.perform(
			get("/secure")
				.with(authentication(authentication))
				.with(request -> {request.setServerPort(8081); return request;})
				.with(request -> {request.setSecure(false); return request;}) //changes nothing
				.with(request -> {request.setScheme("https"); return request;})
		)
			//this shows that Spring Security looks at HttpServletRequest.getScheme
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("You are authenticated")))
		;
	}

	@Test
	@DisplayName("what happens when request comes in over HTTP and is secure but to the right port")
	public void whatHappens2() throws Exception {
		mvc.perform(
			get("/secure")
				.with(authentication(authentication))
				.with(request -> {request.setServerPort(8081); return request;})
				.with(request -> {request.setSecure(true); return request;})
				//notice the scheme has changed,
				//the Web container marked the request as coming in over HTTP
				.with(request -> {request.setScheme("http"); return request;})
		)
			.andExpect(status().is3xxRedirection())
			//looping redirect - results in
			.andExpect(redirectedUrl("https://localhost:8081/secure"))
		;
	}

	@Test
	@DisplayName("what happens when request comes in over HTTPS to the HTTP port")
	public void whatHappens3() throws Exception {
		mvc.perform(
			get("/secure")
				.with(authentication(authentication))
				.with(request -> {request.setServerPort(8080); return request;})
				.with(request -> {request.setSecure(true); return request;})
				.with(request -> {request.setScheme("https"); return request;})
		)
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("You are authenticated")))
		;
	}

}
