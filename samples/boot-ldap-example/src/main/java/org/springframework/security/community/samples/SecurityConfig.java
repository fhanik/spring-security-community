package org.springframework.security.community.samples;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		// @formatter:off
		auth
			.ldapAuthentication()
				.contextSource()
					.url("ldap://localhost:33389")
					.managerDn("cn=admin,ou=Users,dc=test,dc=com")
					.managerPassword("adminsecret")
					.and()
				.userSearchBase("ou=Users,dc=test,dc=com")
				.userSearchFilter("cn={0}")
				.groupSearchBase("dc=test,dc=com")
				.groupSearchFilter("member={0}")
		;
		// @formatter:on
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http
			.authorizeRequests()
				.anyRequest()
				.fullyAuthenticated()
				.and()
			.formLogin()
		;
		// @formatter:on
	}

}
