package br.com.gabxdev.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;


@Component
public class BackendUrlConfig {

    @Value("${rinha.api.back-end-1-url}")
    private String backEndUrl1;

    @Value("${rinha.api.back-end-2-url}")
    private String backEndUrl2;

    public List<URI> getBackendsURI() {
        return List.of(URI.create(backEndUrl1), URI.create(backEndUrl2));
    }
}
