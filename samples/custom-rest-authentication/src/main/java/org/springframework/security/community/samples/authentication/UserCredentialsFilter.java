package org.springframework.security.community.samples.authentication;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class UserCredentialsFilter extends OncePerRequestFilter {
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		final String userCredentials = request.getHeader("X-User-Credentials");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if ("valid-user".equals(userCredentials) && authentication instanceof RestAuthentication) {
			RestAuthentication<SimpleGrantedAuthority> restAuthentication =
				(RestAuthentication<SimpleGrantedAuthority>)authentication;
			restAuthentication.addAuthority(new SimpleGrantedAuthority("USER_CREDENTIALS"));
		}
		filterChain.doFilter(request, response);

	}
}
