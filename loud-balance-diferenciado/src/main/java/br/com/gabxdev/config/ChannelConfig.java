package br.com.gabxdev.config;

import br.com.gabxdev.properties.ApplicationProperties;
import br.com.gabxdev.properties.PropertiesKey;
import org.newsclub.net.unix.AFSocketType;
import org.newsclub.net.unix.AFUNIXDatagramSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

import java.io.File;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;

public class ChannelConfig {

    private final static ChannelConfig INSTANCE = new ChannelConfig();

    private final AFUNIXDatagramSocket api1Client;

    private final AFUNIXDatagramSocket api2Client;

    private final DatagramSocket clientUdp;

    private ChannelConfig() {
        AFUNIXSocketAddress addressApi1;
        AFUNIXSocketAddress addressApi2;

        try {
            addressApi1 = AFUNIXSocketAddress.of(new File("/tmp/api1.sock"));
            addressApi2 = AFUNIXSocketAddress.of(new File("/tmp/api2.sock"));
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        api1Client = createSocketUds(addressApi1);
        api2Client = createSocketUds(addressApi2);
        clientUdp = createSocketUdp();

        startShutdownHook(api1Client);
        startShutdownHook(api2Client);
        startShutdownHook(clientUdp);
    }

    public static ChannelConfig getInstance() {
        return INSTANCE;
    }

    private AFUNIXDatagramSocket createSocketUds(AFUNIXSocketAddress address) {
        try {
            var socket = AFUNIXDatagramSocket.newInstance(AFSocketType.SOCK_DGRAM);
            socket.connect(address);
            socket.setSendBufferSize(5 * 1024 * 1024);
            System.out.println("[ChannelConfig] Create socket unix, address: " + address);
            return socket;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar socket UDS", e);
        }
    }

    private DatagramSocket createSocketUdp() {
        var properties = ApplicationProperties.getInstance();

        var socketPort = Integer.parseInt(properties.getProperty(PropertiesKey.UDP_PORT));

        var hostApi = properties.getProperty(PropertiesKey.BACK_END_1_URL);
        var portApi = Integer.parseInt(properties.getProperty(PropertiesKey.BACK_END_1_PORT));

        try {
            var socket = new DatagramSocket(socketPort);
            socket.setBroadcast(false);
            socket.connect(InetAddress.getByName(hostApi), portApi);
            System.out.println("[ChannelConfig] Create socket udp, port: " + socketPort);
            System.out.println("[ChannelConfig]  Socket UDP connect to: host: " + hostApi + ", port: " + portApi);
            return socket;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar socket UDP", e);
        }
    }

    private void startShutdownHook(DatagramSocket socket) {
        Runtime.getRuntime().addShutdownHook(
                Thread.ofVirtual().unstarted(() -> {
                    try {
                        socket.close();
                    } catch (Exception ignored) {
                    }
                })
        );
    }

    public List<AFUNIXDatagramSocket> getDatagramSockets() {
        return List.of(api1Client, api2Client);
    }

    public DatagramSocket getClientUdp() {
        return clientUdp;
    }
}
