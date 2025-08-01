package br.com.gabxdev.router;

import br.com.gabxdev.config.BackendUrlConfig;
import br.com.gabxdev.config.UdpChannelConfig;
import br.com.gabxdev.lb.LoudBalance;
import br.com.gabxdev.lb.PaymentSummaryWaiter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class SocketRouter {

    private final static SocketRouter INSTANCE = new SocketRouter();

    private final DatagramSocket datagramSocket = UdpChannelConfig.getInstance().getDatagramSocket();

    private final LoudBalance loudBalance = LoudBalance.getInstance();

    private final PaymentSummaryWaiter paymentSummaryWaiter = PaymentSummaryWaiter.getInstance();

    private final BackendUrlConfig backendUrlConfig = BackendUrlConfig.getInstance();

    private SocketRouter() {
        start();
    }

    public static SocketRouter getInstance() {
        return INSTANCE;
    }

    private void start() {
        Thread.startVirtualThread(this::handleEvents);
    }

    private void handleEvents() {
        while (true) {
            try {
                var buffer = new byte[200];

                var datagramPacket = new DatagramPacket(buffer, buffer.length);

                datagramSocket.receive(datagramPacket);

                var event = new String(datagramPacket.getData(), StandardCharsets.UTF_8).trim();

                processEvent(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public void sendToAnyBackend(byte[] eventBytes) {
        var session = loudBalance.selectBackEnd(backendUrlConfig.getBackendsAddresses());

        try {
            datagramSocket.send(new DatagramPacket(eventBytes,
                    eventBytes.length,
                    InetAddress.getByName(session.url()),
                    session.port()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void processEvent(String json) {
        paymentSummaryWaiter.completeResponse(json);
    }
}
