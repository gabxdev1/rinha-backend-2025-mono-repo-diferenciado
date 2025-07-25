package br.com.gabxdev.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

@Configuration
public class LbClientConfig {

    @Value("${rinha.lb.url}")
    private String url;

    @Value("${rinha.lb.port}")
    private int port;

    @Bean
    public SocketAddress lbSocketAddress() {
        return new InetSocketAddress(url, port);
    }
}
