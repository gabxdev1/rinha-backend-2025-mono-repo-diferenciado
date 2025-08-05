package br.com.gabxdev.config;

import java.net.http.HttpClient;

public final class HttpClientConfig {
    private static final HttpClient INSTANCE = HttpClient.newBuilder()
            .followRedirects(java.net.http.HttpClient.Redirect.NEVER)
            .version(HttpClient.Version.HTTP_1_1)
            .executor(Runnable::run)
            .build();

    public static HttpClient httpClient() {
        return INSTANCE;
    }
}
