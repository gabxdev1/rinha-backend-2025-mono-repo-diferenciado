package br.com.gabxdev.router;

import br.com.gabxdev.config.ApiChannelConfig;
import br.com.gabxdev.lb.LoudBalance;
import br.com.gabxdev.lb.PaymentSummaryWaiter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SocketRouter {

    private final static SocketRouter INSTANCE = new SocketRouter();

    private final List<DatagramSocket> sockets = ApiChannelConfig.getInstance().getDatagramSockets();

    private final LoudBalance loudBalance = LoudBalance.getInstance();

    private final PaymentSummaryWaiter paymentSummaryWaiter = PaymentSummaryWaiter.getInstance();

    private SocketRouter() {
        Thread.startVirtualThread(this::handleEvents);
    }

    public static SocketRouter getInstance() {
        return INSTANCE;
    }

    private void handleEvents() {
        while (true) {
            try {
                var buffer = new byte[220];

                var datagramPacket = new DatagramPacket(buffer, buffer.length);

                sockets.getFirst().receive(datagramPacket);

                var event = new String(datagramPacket.getData(), StandardCharsets.UTF_8).trim();

                processEvent(event);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void sendEvent(byte[] eventBytes) {
        try {
            loudBalance.selectBackEnd(sockets).send(new DatagramPacket(eventBytes, eventBytes.length));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendEventOfSummary(byte[] eventBytes) {
        try {
            sockets.getFirst().send(new DatagramPacket(eventBytes, eventBytes.length));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void processEvent(String json) {
        paymentSummaryWaiter.completeResponse(json);
    }
}
