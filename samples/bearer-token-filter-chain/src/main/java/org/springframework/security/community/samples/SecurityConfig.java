package org.springframework.security.community.samples;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Bean
	AuthenticationFailureHandler failureHandler() {
		return new SimpleUrlAuthenticationFailureHandler();
	}

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
			.formLogin()
				.failureHandler(failureHandler())
				.and()
			.csrf().disable()

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

	@Bean
	public JwtDecoder jwtDecoder() {
		return token -> {
			throw new UnsupportedOperationException("You must replace the `jwtDecoder` bean");
		};
	}
}
