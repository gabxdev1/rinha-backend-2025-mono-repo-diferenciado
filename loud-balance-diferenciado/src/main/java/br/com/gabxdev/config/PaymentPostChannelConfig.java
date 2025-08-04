package br.com.gabxdev.config;

import br.com.gabxdev.socket.BackendAddress;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

public class PaymentPostChannelConfig {

    private final static PaymentPostChannelConfig INSTANCE = new PaymentPostChannelConfig();

    private final DatagramSocket datagramSocketApi1;

    private final DatagramSocket datagramSocketApi2;

    private PaymentPostChannelConfig() {
        var backendUrlConfig = BackendUrlConfig.getInstance().getBackendsAddresses();

        datagramSocketApi1 = this.loadDatagramSocket(backendUrlConfig.getFirst());

        datagramSocketApi2 = this.loadDatagramSocket(backendUrlConfig.getLast());
    }

    public static PaymentPostChannelConfig getInstance() {
        return INSTANCE;
    }

    private DatagramSocket loadDatagramSocket(BackendAddress backendUrlConfig) {
        DatagramSocket datagramSocket;

        try {
            datagramSocket = new DatagramSocket();
            datagramSocket.setBroadcast(false);
            datagramSocket.setSendBufferSize(1024 * 2);
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
