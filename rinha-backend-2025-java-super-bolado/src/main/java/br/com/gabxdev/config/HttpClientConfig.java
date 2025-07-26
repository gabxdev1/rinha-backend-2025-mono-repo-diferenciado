package br.com.gabxdev.config;

import java.net.http.HttpClient;

public final class HttpClientConfig {
    private static final HttpClient INSTANCE = HttpClient.newBuilder()
//            .connectTimeout(Duration.ofMillis(600))
            .followRedirects(java.net.http.HttpClient.Redirect.NEVER)
            .version(java.net.http.HttpClient.Version.HTTP_1_1)
            .executor(Runnable::run)
            .build();

    public static HttpClient httpClient() {
        return INSTANCE;
    }
}
