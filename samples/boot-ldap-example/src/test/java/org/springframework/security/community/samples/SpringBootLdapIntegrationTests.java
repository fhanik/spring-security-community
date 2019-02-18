package org.springframework.security.community.samples;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("spring security LDAP tests")
public class SpringBootLdapIntegrationTests {

	@Autowired
	private MockMvc mvc;

	private static ApacheDsSSLContainer apacheDS;

	@BeforeAll
	static void startLdapServer() throws Exception {
		apacheDS = ApacheDSHelper.start();
	}

	@AfterAll
	public static void stopLdapServer() {
		apacheDS.stop();
	}


	@Test
	@DisplayName("pages are secured")
	void pageIsSecure() throws Exception {
		mvc.perform(
			MockMvcRequestBuilders.get("/")
		)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("http://localhost/login"))
			.andExpect(unauthenticated())
		;
	}

	@Test
	@DisplayName("ldap login works")
	void doLogin() throws Exception {
		mvc.perform(
			MockMvcRequestBuilders.post("/login")
				.param("username", "marissa")
				.param("password", "koala")
				.with(csrf())
		)
			.andExpect(status().is3xxRedirection())
			.andExpect(authenticated())
		;
	}

}
