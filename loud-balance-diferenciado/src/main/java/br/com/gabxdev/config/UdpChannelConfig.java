package br.com.gabxdev.config;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UdpChannelConfig {

    private final static UdpChannelConfig INSTANCE = new UdpChannelConfig();

    private DatagramSocket datagramSocket;

    private UdpChannelConfig() {
        var host = BackendUrlConfig.getInstance().getBackendsAddresses().getFirst();

        try {
            this.datagramSocket = new DatagramSocket();
            datagramSocket.setBroadcast(false);
            datagramSocket.connect(InetAddress.getByName(host.url()), host.port());
            startShutdownHook(datagramSocket);
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
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
