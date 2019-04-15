package sample.web;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.DefaultRedirectStrategy;

@EnableWebSecurity
public class SecurityConfig  extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http
			//application security
			.authorizeRequests()
				.anyRequest().fullyAuthenticated()
				.and()
			.oauth2Login()
			.successHandler(
				(request,response,authentication) -> {
					System.out.println("Success Handler Invoked");
					new DefaultRedirectStrategy().sendRedirect(
						request,
						response,
						"/"
					);
				}
			)
		;
		// @formatter:on
	}

}
