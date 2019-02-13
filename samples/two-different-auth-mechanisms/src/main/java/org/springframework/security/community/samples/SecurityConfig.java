package org.springframework.security.community.samples;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.header.HeaderWriterFilter;

import static org.springframework.security.core.userdetails.User.builder;

@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder(){
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new InMemoryUserDetailsManager(
			builder()
				.username("user")
				.password("{bcrypt}$2a$10$C8c78G3SRJpy268vInPUFu.3lcNHG9SaNAPdSaIOy.1TJIio0cmTK")
				.roles("USER")
				.build(),
			builder()
				.username("admin")
				.password("{bcrypt}$2a$10$XvWhl0acx2D2hvpOPd/rPuPA48nQGxOFom1NqhxNN9ST1p9lla3bG")
				.roles("ADMIN")
				.build()
		) {
			@Override
			public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
				System.out.println("Custom User Details: Loading user by username:"+username);
				return super.loadUserByUsername(username);
			}
		};
	}


	@Configuration
	@Order(1)
	public static class ApiSecurity extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			// @formatter:off
			http
				.mvcMatcher("/api/**")
				.authorizeRequests()
					.anyRequest().access("hasAnyAuthority('ADMIN','USER') and hasAuthority('API_KEY')")
					.and()
				.addFilterAfter(new ApiKeyFilter(), HeaderWriterFilter.class)
				.sessionManagement().disable()
			;
			// @formatter:on
		}
	}

	@Configuration
	@Order(2)
	public static class WebSecurity extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			// @formatter:off
			http
				//application security
				.mvcMatcher("/**")
				.authorizeRequests()
					.anyRequest().hasAnyRole("ADMIN","USER")
					.and()
				.httpBasic()
					.and()
				.formLogin()
			;
			// @formatter:on
		}
	}


}
