package org.springframework.security.community.samples;

import org.springframework.security.community.samples.authentication.ApiKeyFilter;
import org.springframework.security.community.samples.authentication.UserCredentialsFilter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.header.HeaderWriterFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http
			.sessionManagement()
				.disable()
			//application security
			.authorizeRequests()
				.anyRequest().hasAuthority("API_KEY")
				.and()
		    .addFilterBefore(new ApiKeyFilter(), HeaderWriterFilter.class)
			.addFilterAfter(new UserCredentialsFilter(), ApiKeyFilter.class)
			.csrf().ignoringAntMatchers(
				"/api-key-only",
				"/dual-auth"
		)
		;
		// @formatter:on
	}

}
