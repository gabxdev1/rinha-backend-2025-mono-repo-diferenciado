package br.com.gabxdev.config;

import br.com.gabxdev.properties.ApplicationProperties;
import br.com.gabxdev.properties.PropertiesKey;

import java.net.DatagramSocket;
import java.net.SocketException;


public final class DatagramSocketExternalConfig {

    private final static DatagramSocketExternalConfig INSTANCE = new DatagramSocketExternalConfig();

    private final DatagramSocket datagramSocket;

    private final int udpChannelPort;

    private DatagramSocketExternalConfig() {
        var applicationProperties = ApplicationProperties.getInstance();

        var udpChannelPortS = applicationProperties.getProperty(PropertiesKey.UDP_CHANNEL_INTERNAL_PORT);

        this.udpChannelPort = Integer.parseInt(udpChannelPortS);

        this.datagramSocket = loadDatagramSocket();
    }

    private DatagramSocket loadDatagramSocket() {
        DatagramSocket datagramSocket;

        try {
            datagramSocket = new DatagramSocket(udpChannelPort);
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

    public static DatagramSocketExternalConfig getInstance() {
        return INSTANCE;
    }

    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }

    public int getUdpChannelPort() {
        return udpChannelPort;
    }
}
