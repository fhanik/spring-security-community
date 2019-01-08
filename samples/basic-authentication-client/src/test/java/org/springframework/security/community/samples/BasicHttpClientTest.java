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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.util.StringUtils.hasText;

public class BasicHttpClientTest {

	private static ClientAndServer server;

	@BeforeAll
	public static void mockServer() {
		server = ClientAndServer.startClientAndServer(8811);
		server
			.when(
				HttpRequest.request()
			)
			.respond(request -> HttpResponse.response()
				.withHeader(new Header("Content-Type", "text/plain; charset=utf-8"))
				.withBody("Authorization header is" +
					(hasText(request.getFirstHeader("Authorization")) ? "" : " NOT") +
					" present."
				)
			);
	}

	@AfterAll
	static void shutdownMockServer() {
		server.stop();
	}

	@Test
	public void testHttpClientWithoutAuthHeader() throws Exception {
		final String noHeaderExpected =
			BasicAuthorizationClient.ApacheHttpClient(
				"test",
				"test",
				"http://localhost:8811",
				false
			);
		System.out.println(noHeaderExpected);
		assertThat(noHeaderExpected, equalTo("Authorization header is NOT present."));
	}

	@Test
	public void testHttpClientWithAuthHeader() throws Exception {
		final String noHeaderExpected =
			BasicAuthorizationClient.ApacheHttpClient(
				"test",
				"test",
				"http://localhost:8811",
				true
			);
		System.out.println(noHeaderExpected);
		assertThat(noHeaderExpected, equalTo("Authorization header is present."));
	}

}
