package br.com.gabxdev.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class HttpClientConfig {

    @Value("${rinha.api.backend}")
    public String apiBackEndClientUrl;

    @Bean
    public WebClient apiPaymentProcessor(WebClient.Builder webClientBuilder) {
        var httpClient = HttpClient.create()
                .keepAlive(true)
                .compress(false)
                .wiretap(false);

        return webClientBuilder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public RestClient apiInternalClient() {
        return RestClient.builder()
                .baseUrl(apiBackEndClientUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
