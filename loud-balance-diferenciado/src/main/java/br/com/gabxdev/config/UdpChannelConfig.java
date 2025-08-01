package br.com.gabxdev.config;

import java.net.DatagramSocket;
import java.net.SocketException;

public class UdpChannelConfig {

    private final static UdpChannelConfig INSTANCE = new UdpChannelConfig();

    private final DatagramSocket datagramSocket;

    private UdpChannelConfig() {
        try {
            datagramSocket = new DatagramSocket();
            datagramSocket.setSendBufferSize(4 * 1024 * 1024);
            datagramSocket.setBroadcast(false);
            startShutdownHook(datagramSocket);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public static UdpChannelConfig getInstance() {
        return INSTANCE;
    }

    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }

    private void startShutdownHook(DatagramSocket channel) {
        Runtime.getRuntime().addShutdownHook(
                Thread.ofVirtual().unstarted(channel::close)
        );
    }
}
