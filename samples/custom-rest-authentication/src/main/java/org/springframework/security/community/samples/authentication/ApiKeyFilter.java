package org.springframework.security.community.samples.authentication;

import java.io.IOException;
import java.util.Collections;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import static org.springframework.util.StringUtils.hasText;

public class ApiKeyFilter extends OncePerRequestFilter {
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		final String authorization = request.getHeader("Authorization");
		final String prefix = "ApiKey ";
		if (hasText(authorization) && authorization.startsWith(prefix)) {
			String key = authorization.substring(prefix.length());
			if ("this-is-a-valid-key".equals(key)) {
				RestAuthentication<SimpleGrantedAuthority> authentication = new RestAuthentication<>(
					key,
					Collections.singletonList(new SimpleGrantedAuthority("API_KEY"))
				);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		filterChain.doFilter(request, response);

	}
}
