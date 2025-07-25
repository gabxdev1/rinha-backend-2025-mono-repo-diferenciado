package br.com.gabxdev.client;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

@Component
public class LoadBalanceClient {

    private final SocketAddress lbSocketAddress;

    private final DatagramChannel channel;

    public LoadBalanceClient(SocketAddress lbSocketAddress, DatagramChannel channel) {
        this.lbSocketAddress = lbSocketAddress;
        this.channel = channel;
    }

    public void sendEventLb(byte[] payload) {
        try {
            channel.send(ByteBuffer.wrap(payload), lbSocketAddress);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
