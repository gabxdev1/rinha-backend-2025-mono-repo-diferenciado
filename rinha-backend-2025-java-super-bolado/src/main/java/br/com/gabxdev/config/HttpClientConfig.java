package br.com.gabxdev.config;

import java.net.http.HttpClient;
import java.time.Duration;

public final class HttpClientConfig {
    private static final HttpClient INSTANCE = HttpClient.newBuilder()
//            .connectTimeout(Duration.ofMillis(350))
            .followRedirects(java.net.http.HttpClient.Redirect.NEVER)
            .version(HttpClient.Version.HTTP_1_1)
            .executor(Runnable::run)
            .build();

    public static HttpClient httpClient() {
        return INSTANCE;
    }
}
