package br.com.gabxdev.config;

import java.net.http.HttpClient;
import java.time.Duration;

public final class HttpClientConfig {
    private static final HttpClient INSTANCE = HttpClient.newBuilder()
            .followRedirects(java.net.http.HttpClient.Redirect.NEVER)
            .connectTimeout(Duration.ofSeconds(1))
            .version(HttpClient.Version.HTTP_2)
            .executor(Runnable::run)
            .build();

    public static HttpClient httpClient() {
        return INSTANCE;
    }
}
