package org.springframework.security.community.samples;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

public class CustomTokenEnhancer implements TokenEnhancer {

	// Add user information to the token
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		String name = "Joe Schmoe";
		Map<String, Object> info = new LinkedHashMap<String, Object>(accessToken.getAdditionalInformation());
		info.put("email", "Joe@Schmoe.Com");
		info.put("full_name", name);
		info.put("scopes", authentication.getAuthorities().stream().map(s -> s.toString()).collect(Collectors.toList()));
		info.put("organization", authentication.getName());
		DefaultOAuth2AccessToken customAccessToken = new DefaultOAuth2AccessToken(accessToken);
		customAccessToken.setAdditionalInformation(info);
		customAccessToken.setExpiration(accessToken.getExpiration());
		return customAccessToken;
	}

}