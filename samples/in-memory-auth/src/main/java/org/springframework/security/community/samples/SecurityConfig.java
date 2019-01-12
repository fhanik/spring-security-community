package org.springframework.security.community.samples;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import static org.springframework.security.core.userdetails.User.builder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Bean
	public PasswordEncoder passwordEncoder(){
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new InMemoryUserDetailsManager(
			builder()
				.passwordEncoder(input -> passwordEncoder().encode(input))
				.username("user")
				.password("123")
				.roles("USER")
				.build(),
			builder()
				.passwordEncoder(input -> passwordEncoder().encode(input))
				.username("admin")
				.password("password")
				.roles("USER", "ADMIN")
				.build()
		);
	}


	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http
			//application security
			.authorizeRequests()
				.mvcMatchers("/non-secure/**").permitAll()
				.anyRequest().fullyAuthenticated()
				.and()
			.formLogin()
		;
		// @formatter:on
	}

}
