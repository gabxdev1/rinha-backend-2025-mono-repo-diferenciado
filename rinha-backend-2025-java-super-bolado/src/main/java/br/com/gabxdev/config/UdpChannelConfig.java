package br.com.gabxdev.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.*;

@Configuration
public class UdpChannelConfig {

    @Value("${rinha.api.udp-channel-port}")
    private int udpChannelPort;

    @Bean
    public DatagramSocket datagramSocket() throws SocketException {
        var datagramSocket = new DatagramSocket(udpChannelPort);

        startShutdownHook(datagramSocket);

        return datagramSocket;
    }


    private void startShutdownHook(DatagramSocket channel) {
        Runtime.getRuntime().addShutdownHook(
                Thread.ofVirtual().unstarted(channel::close)
        );
    }
}
