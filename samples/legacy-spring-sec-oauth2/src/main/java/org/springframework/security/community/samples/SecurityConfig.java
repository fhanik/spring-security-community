package org.springframework.security.community.samples;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.InMemoryClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import static org.springframework.security.core.userdetails.User.builder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Bean
	public PasswordEncoder passwordEncoder(){
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean //by exposing this bean, password grant becomes enabled
	public AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService());
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new InMemoryUserDetailsManager(
			builder()
				.username("user")
				.password("{bcrypt}$2a$10$C8c78G3SRJpy268vInPUFu.3lcNHG9SaNAPdSaIOy.1TJIio0cmTK") //123
				.roles("USER")
				.build(),
			builder()
				.username("admin")
				.password("{bcrypt}$2a$10$XvWhl0acx2D2hvpOPd/rPuPA48nQGxOFom1NqhxNN9ST1p9lla3bG") //password
				.roles("ADMIN")
				.build()
		);
	}

	@EnableAuthorizationServer
	public static class Oauth2SecurityConfig extends AuthorizationServerConfigurerAdapter {
		private final PasswordEncoder passwordEncoder;
		private final AuthenticationManager authenticationManager;

		public Oauth2SecurityConfig(PasswordEncoder passwordEncoder,
									AuthenticationManager authenticationManager) {
			this.passwordEncoder = passwordEncoder;
			this.authenticationManager = authenticationManager;
		}

		@Bean
		public TokenEnhancer tokenEnhancer() {
			return new CustomTokenEnhancer();
		}

		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
			endpoints
				.tokenEnhancer(tokenEnhancer())
				.authenticationManager(authenticationManager)
			;

		}

		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			InMemoryClientDetailsService clientDetails = new InMemoryClientDetailsService();
			BaseClientDetails client = new BaseClientDetails(
				"testclient",
				null,
				"testscope,USER,ADMIN",
				"password",
				null
			);
			client.setClientSecret(passwordEncoder.encode("secret"));
			clientDetails.setClientDetailsStore(
				Collections.singletonMap(
					client.getClientId(),
					client
				)
			);
			clients.withClientDetails(clientDetails);
		}

	}

}
