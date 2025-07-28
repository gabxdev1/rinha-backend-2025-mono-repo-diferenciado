package br.com.gabxdev.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.DatagramSocket;
import java.net.SocketException;

@Configuration
public class UdpChannelConfig {

    @Bean
    public DatagramSocket datagramSocket() throws SocketException {
        var datagramSocket = new DatagramSocket();
        datagramSocket.setSendBufferSize(4 * 1024 * 1024);
        datagramSocket.setBroadcast(false);
        startShutdownHook(datagramSocket);

        return datagramSocket;
    }

    private void startShutdownHook(DatagramSocket channel) {
        Runtime.getRuntime().addShutdownHook(
                Thread.ofVirtual().unstarted(channel::close)
        );
    }
}
