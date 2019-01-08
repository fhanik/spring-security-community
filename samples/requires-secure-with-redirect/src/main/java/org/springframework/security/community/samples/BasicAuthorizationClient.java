package org.springframework.security.community.samples;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class BasicAuthorizationClient {

	public static String ApacheHttpClient(String userID,
										String userPWD,
										String url,
										boolean preempt) throws Exception {
		CredentialsProvider provider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(userID, userPWD);
		provider.setCredentials(AuthScope.ANY, credentials);
		HttpClientContext context = HttpClientContext.create();

		HttpClientBuilder builder = HttpClientBuilder.create();
		if (preempt) {
			AuthCache authCache = new BasicAuthCache();
			authCache.put(HttpHost.create(url), new BasicScheme());
			context.setCredentialsProvider(provider);
			context.setAuthCache(authCache);
		} else {
			builder.setDefaultCredentialsProvider(provider);
		}
		HttpClient client = builder.build();
		try {
			HttpResponse response = client.execute(new HttpGet(url), context);
			int statusCode = response.getStatusLine().getStatusCode();
			System.out.println("Response Status Code : " + statusCode);
			return EntityUtils.toString(response.getEntity());
		} finally {
			client.getConnectionManager().shutdown();
		}
	}
}
