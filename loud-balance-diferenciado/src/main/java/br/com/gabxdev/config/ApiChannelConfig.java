package br.com.gabxdev.config;

import br.com.gabxdev.socket.BackendAddress;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

public class ApiChannelConfig {

    private final static ApiChannelConfig INSTANCE = new ApiChannelConfig();

    private final DatagramSocket datagramSocketApi1;

    private final DatagramSocket datagramSocketApi2;

    private ApiChannelConfig() {
        var backendUrlConfig = BackendUrlConfig.getInstance().getBackendsAddresses();

        datagramSocketApi1 = this.loadDatagramSocket(backendUrlConfig.getFirst(), 9096);

        datagramSocketApi2 = this.loadDatagramSocket(backendUrlConfig.getLast(), null);
    }

    public static ApiChannelConfig getInstance() {
        return INSTANCE;
    }

    private DatagramSocket loadDatagramSocket(BackendAddress backendUrlConfig, Integer port) {
        DatagramSocket datagramSocket;

        try {
            if (port != null) {
                datagramSocket = new DatagramSocket(port);
            } else {
                datagramSocket = new DatagramSocket();
            }

            datagramSocket.setBroadcast(false);
            datagramSocket.setSendBufferSize(5 * 1024 * 1024);
            datagramSocket.connect(InetAddress.getByName(backendUrlConfig.url()), backendUrlConfig.port());
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

    public List<DatagramSocket> getDatagramSockets() {
        return List.of(datagramSocketApi1, datagramSocketApi2);
    }



}
