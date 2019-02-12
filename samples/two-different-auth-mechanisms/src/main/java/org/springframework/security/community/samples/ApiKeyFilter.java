package org.springframework.security.community.samples;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import static java.util.Arrays.asList;
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
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					key,
					authorization,
					asList(
						new SimpleGrantedAuthority("API_KEY"),
						new SimpleGrantedAuthority("ADMIN")
					)
				);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		filterChain.doFilter(request, response);

	}
}
