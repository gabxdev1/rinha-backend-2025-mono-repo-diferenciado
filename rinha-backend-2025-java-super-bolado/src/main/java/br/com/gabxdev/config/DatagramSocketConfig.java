package br.com.gabxdev.config;

import br.com.gabxdev.properties.ApplicationProperties;
import br.com.gabxdev.properties.PropertiesKey;

import java.net.DatagramSocket;
import java.net.SocketException;

public final class DatagramSocketConfig {

    private final static DatagramSocketConfig INSTANCE = new DatagramSocketConfig();

    private final DatagramSocket datagramSocket;

    private final int udpChannelPort;

    private DatagramSocketConfig() {
        var applicationProperties = ApplicationProperties.getInstance();

        var udpChannelPortS = applicationProperties.getProperty(PropertiesKey.UDP_CHANNEL_PORT);

        this.udpChannelPort = Integer.parseInt(udpChannelPortS);

        this.datagramSocket = loadDatagramSocket();
    }

    private DatagramSocket loadDatagramSocket() {
        DatagramSocket datagramSocket;

        try {
            datagramSocket = new DatagramSocket(udpChannelPort);
            datagramSocket.setReceiveBufferSize(5 * 1024 * 1024);
            datagramSocket.setBroadcast(false);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        startShutdownHook(datagramSocket);

        return datagramSocket;
    }

    private void startShutdownHook(DatagramSocket channel) {
        Runtime.getRuntime().addShutdownHook(
                Thread.ofVirtual().unstarted(channel::close)
        );
    }

    public static DatagramSocketConfig getInstance() {
        return INSTANCE;
    }

    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }
}
