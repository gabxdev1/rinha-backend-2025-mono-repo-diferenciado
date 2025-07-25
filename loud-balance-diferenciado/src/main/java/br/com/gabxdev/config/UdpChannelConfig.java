package br.com.gabxdev.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

@Configuration
public class UdpChannelConfig {

    @Bean
    public  DatagramChannel datagramChannel() throws IOException {
        var channel = DatagramChannel.open();

        channel.configureBlocking(false);

        channel.bind(new InetSocketAddress(4000));

        return channel;
    }
}
