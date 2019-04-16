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

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.OncePerRequestFilter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("customize filter chain for test")
class CustomizeFilterChainTests {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private WebApplicationContext context;

	@SpringBootConfiguration
	@EnableAutoConfiguration
	@Import({SecurityConfig.class, SampleAppController.class})
	public static class SpringBootApplicationTestConfig {
		@Bean("customSecurityFilterChainPostProcessor")
		BeanPostProcessor securityFilterChainPostProcessor() {
			return new SecurityFilterChainPostProcessor();
		}
	}

	@Test
	@DisplayName("ensure my five hundred filter is working")
	void myTestFilterChainIsInvoked() throws Exception {
		mvc.perform(
			get("/api-key-only")
				.header("Authorization", "ApiKey this-is-a-valid-key")
		)
			.andExpect(status().is5xxServerError())
			.andExpect(content().string("TEST FILTER CHAIN"))
		;
	}

	static class SecurityFilterChainPostProcessor implements BeanPostProcessor {
		@Override
		public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
			if ("springSecurityFilterChain".equals(beanName)) {
				FilterChainProxy fcp = (FilterChainProxy) bean;
				for (SecurityFilterChain fc : fcp.getFilterChains()) {
					fc.getFilters().add(0, new FiveHundredFilter());
				}
			}
			return bean;
		}
	}

	private static class FiveHundredFilter extends OncePerRequestFilter {
		@Override
		protected void doFilterInternal(HttpServletRequest request,
										HttpServletResponse response,
										FilterChain filterChain) throws ServletException, IOException {
			response.setStatus(500);
			response.getWriter().write("TEST FILTER CHAIN");
		}
	}


}
