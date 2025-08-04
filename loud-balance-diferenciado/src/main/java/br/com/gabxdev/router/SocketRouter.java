package br.com.gabxdev.router;

import br.com.gabxdev.config.BackendUrlConfig;
import br.com.gabxdev.config.PaymentPostChannelConfig;
import br.com.gabxdev.config.UdpChannelConfig;
import br.com.gabxdev.lb.LoudBalance;
import br.com.gabxdev.lb.PaymentSummaryWaiter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SocketRouter {

    private final static SocketRouter INSTANCE = new SocketRouter();

    private final DatagramSocket socketSummaryAndPurge = UdpChannelConfig.getInstance().getDatagramSocket();

    private final List<DatagramSocket> sockets = PaymentPostChannelConfig.getInstance().getDatagramSockets();

    private final LoudBalance loudBalance = LoudBalance.getInstance();

    private final PaymentSummaryWaiter paymentSummaryWaiter = PaymentSummaryWaiter.getInstance();

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
                var buffer = new byte[180];

                var datagramPacket = new DatagramPacket(buffer, buffer.length);

                socketSummaryAndPurge.receive(datagramPacket);

                var event = new String(datagramPacket.getData(), StandardCharsets.UTF_8).trim();

                processEvent(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public void sendEventPost(byte[] eventBytes) {
        try {
            loudBalance.selectBackEnd(sockets).send(new DatagramPacket(eventBytes, eventBytes.length));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendEvent(byte[] eventBytes) {
        try {
            socketSummaryAndPurge.send(new DatagramPacket(eventBytes, eventBytes.length));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void processEvent(String json) {
        paymentSummaryWaiter.completeResponse(json);
    }
}
