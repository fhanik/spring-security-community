package org.springframework.security.community.samples;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
	http
		//application security
		.authorizeRequests()
			.mvcMatchers("/non-secure/**").permitAll()
			.anyRequest()
				.hasAnyAuthority("ROLE_USER","SCOPE_USER")
				.and()
		.oauth2ResourceServer()
			.jwt()
				.and()
			.and()
		.formLogin();
	;
	// @formatter:on
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new InMemoryUserDetailsManager(
			User.withDefaultPasswordEncoder()
				.username("user")
				.password("password")
				.roles("USER")
				.build()
		);
	}
}
