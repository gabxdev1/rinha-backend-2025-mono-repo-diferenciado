package br.com.gabxdev.config;

import br.com.gabxdev.properties.ApplicationProperties;
import br.com.gabxdev.properties.PropertiesKey;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class SocketInternalConfig {

    private final static SocketInternalConfig INSTANCE = new SocketInternalConfig();

    private final DatagramSocket datagramSocket;

    private final int udpChannelPort;

    private final String hostApi2;

    private final int portApi2;

    private SocketInternalConfig() {
        var properties = ApplicationProperties.getInstance();
        var externalHost = BackendInternalHostConfig.getInstance();

        hostApi2 = externalHost.getBackEndExternalHost();
        portApi2 = externalHost.getBackendExternalPort();

        var udpChannelPortS = properties.getProperty(PropertiesKey.UDP_CHANNEL_INTERNAL_PORT);

        this.udpChannelPort = Integer.parseInt(udpChannelPortS);

        this.datagramSocket = loadDatagramSocket();
    }

    private DatagramSocket loadDatagramSocket() {
        DatagramSocket datagramSocket;

        try {
            datagramSocket = new DatagramSocket(udpChannelPort);
            datagramSocket.setBroadcast(false);
            datagramSocket.connect(InetAddress.getByName(this.hostApi2), this.portApi2);
        } catch (SocketException | UnknownHostException e) {
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

    public static SocketInternalConfig getInstance() {
        return INSTANCE;
    }

    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }
}
