package br.com.gabxdev.config;

import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebSocketConfig {

    @Bean
    public ReactorNettyWebSocketClient reactorNettyWebSocketClient() {

        var httpClient = HttpClient.create()
                .option(ChannelOption.TCP_NODELAY, false)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_REUSEADDR, false)
                .compress(false);

        return new ReactorNettyWebSocketClient(httpClient);
    }
}
