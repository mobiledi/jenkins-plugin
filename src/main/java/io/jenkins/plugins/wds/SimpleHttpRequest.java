package io.jenkins.plugins.wds;

import hudson.ProxyConfiguration;
import jenkins.model.Jenkins;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.util.EntityUtils;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleHttpRequest {
    private static final Logger logger = Logger.getLogger(SimpleHttpRequest.class.getName());

    static boolean get(String url) {
        HttpGet get = new HttpGet(url);

        return request(get);
    }

    static boolean post(String url) {
        return post(url, Collections.emptyList());
    }

    static boolean post(String url, List<NameValuePair> params) {
        HttpPost post = new HttpPost(url);

        try {
            post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            return request(post);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Post request error", e);
            return false;
        }
    }

    static boolean request(HttpRequestBase req) {
        CloseableHttpClient client = getHttpClient();

        try {
            CloseableHttpResponse response = client.execute(req);

            int responseCode = response.getStatusLine().getStatusCode();
            if(responseCode != HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity);
                logger.log(Level.WARNING, "WDS post may have failed. Response: " + responseString);
                logger.log(Level.WARNING, "Response Code: " + responseCode);
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Request error", e);
            return false;
        } finally {
            req.releaseConnection();
        }

    }

    static protected CloseableHttpClient getHttpClient() {
        final HttpClientBuilder clientBuilder = HttpClients.custom();
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        clientBuilder.setDefaultCredentialsProvider(credentialsProvider);

        if (Jenkins.getInstance() != null) {
            ProxyConfiguration proxy = Jenkins.getInstance().proxy;
            if (proxy != null) {
                final HttpHost proxyHost = new HttpHost(proxy.name, proxy.port);
                final HttpRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxyHost);
                clientBuilder.setRoutePlanner(routePlanner);

                String username = proxy.getUserName();
                String password = proxy.getPassword();
                // Consider it to be passed if username specified. Sufficient?
                if (username != null && !"".equals(username.trim())) {
                    credentialsProvider.setCredentials(
                            new AuthScope(proxyHost),
                            new UsernamePasswordCredentials(username, password)
                    );
                }
            }
        }
        return clientBuilder.build();
    }
}
