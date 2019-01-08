package org.springframework.security.community.samples;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
	http
		//application security
		.authorizeRequests()
			.mvcMatchers("/non-secure/**").permitAll()
			.anyRequest().hasRole("USER")
			.and()
		.formLogin()
			.and()
		.requiresChannel().anyRequest().requiresSecure()
			.and()
		.portMapper().http(8080).mapsTo(8081)
	;
	// @formatter:on
	}

}
