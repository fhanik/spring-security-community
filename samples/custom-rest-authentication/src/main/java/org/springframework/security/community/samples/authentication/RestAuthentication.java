package org.springframework.security.community.samples.authentication;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class RestAuthentication<T extends GrantedAuthority> implements Authentication {

	private final String apiKey;
	private Object userCredential;
	private List<T> authorities;

	public RestAuthentication(String apiKey, List<T> authorities) {
		this.authorities = new LinkedList<>(authorities);
		this.apiKey = apiKey;
	}

	void addAuthority(T authority) {
		authorities.add(authority);
	}

	@Override
	public Collection<T> getAuthorities() {
		return authorities;
	}

	@Override
	public Object getCredentials() {
		return userCredential;
	}

	void setUserCredentials(Object credentials) {
		this.userCredential = credentials;
	}

	@Override
	public Object getDetails() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return getName();
	}

	@Override
	public boolean isAuthenticated() {
		return true;
	}

	@Override
	public String getName() {
		return apiKey;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		throw new IllegalArgumentException();
	}
}
