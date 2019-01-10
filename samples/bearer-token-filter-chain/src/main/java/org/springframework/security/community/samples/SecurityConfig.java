package org.springframework.security.community.samples;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

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
			.httpBasic()
				.and()
			.csrf().disable()
			.addFilterBefore(new HealthFilter(), HeaderWriterFilter.class)
		;
		// @formatter:on
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new InMemoryUserDetailsManager(
			User.builder()
				.passwordEncoder(input -> passwordEncoder().encode(input))
				.username("user")
				.password("{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG")
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

	static class HealthFilter extends OncePerRequestFilter {

		private String healthPath = "/healthcheck/**";
		private AntPathRequestMatcher matcher = new AntPathRequestMatcher(healthPath);

		@Override
		protected void doFilterInternal(HttpServletRequest request,
										HttpServletResponse response,
										FilterChain filterChain) throws ServletException, IOException {

			if (matcher.matches(request)) {
				//do anything you want over here.
				//including performing your health check
				response.getWriter().write("OK");
				response.setStatus(200);
			}
			else {
				//only execute the other filters if we're not doing a health check
				filterChain.doFilter(request, response);
			}

		}
	}
}
