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

    private final AFUNIXDatagramSocket apiOneOneClient;
    private final AFUNIXDatagramSocket apiOneTwoClient;

    private final AFUNIXDatagramSocket apiTwoOneClient;
    private final AFUNIXDatagramSocket apiTwoTwoClient;

    private final DatagramSocket clientUdp;

    private ChannelConfig() {
        AFUNIXSocketAddress addressApiOneOne;
        AFUNIXSocketAddress addressApiOneTwo;
        AFUNIXSocketAddress addressApiTwoOne;
        AFUNIXSocketAddress addressApiTwoTwo;

        try {
            addressApiOneOne = AFUNIXSocketAddress.of(new File("/tmp/api1-1.sock"));
            addressApiOneTwo = AFUNIXSocketAddress.of(new File("/tmp/api1-2.sock"));
            addressApiTwoOne = AFUNIXSocketAddress.of(new File("/tmp/api2-1.sock"));
            addressApiTwoTwo = AFUNIXSocketAddress.of(new File("/tmp/api2-2.sock"));
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        apiOneOneClient = createSocketUds(addressApiOneOne);
        apiOneTwoClient = createSocketUds(addressApiOneTwo);
        apiTwoOneClient = createSocketUds(addressApiTwoOne);
        apiTwoTwoClient = createSocketUds(addressApiTwoTwo);
        clientUdp = createSocketUdp();

        startShutdownHook(List.of(apiOneOneClient, apiOneTwoClient, apiTwoOneClient, apiTwoTwoClient, clientUdp));
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
            throw new RuntimeException("Erro ao criar socket UDS: " + e.getMessage(), e);
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

    private void startShutdownHook(List<DatagramSocket> socket) {
        Runtime.getRuntime().addShutdownHook(
                Thread.ofVirtual().unstarted(() -> {
                    try {
                        socket.forEach(DatagramSocket::close);
                    } catch (Exception ignored) {
                    }
                })
        );
    }

    public List<AFUNIXDatagramSocket> getDatagramSockets() {
        return List.of(apiOneOneClient, apiTwoOneClient, apiOneTwoClient, apiTwoTwoClient);
    }

    public DatagramSocket getClientUdp() {
        return clientUdp;
    }
}
