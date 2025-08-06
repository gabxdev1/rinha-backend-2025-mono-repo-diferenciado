package br.com.gabxdev.consumer;

import br.com.gabxdev.config.ChannelConfig;
import br.com.gabxdev.lb.PaymentSummaryWaiter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class PaymentSummaryConsumer {

    private final static PaymentSummaryConsumer INSTANCE = new PaymentSummaryConsumer();

    private final DatagramSocket client = ChannelConfig.getInstance().getClientUdp();

    private PaymentSummaryConsumer() {
        Thread.startVirtualThread(() -> {
            try {
                handlePaymentSummary();
            } catch (IOException e) {
                System.out.println("Error handlePaymentSummary: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public static PaymentSummaryConsumer getInstance() {
        return INSTANCE;
    }

    private void handlePaymentSummary() throws IOException {
        var paymentSummaryWaiter = PaymentSummaryWaiter.getInstance();

        var buffer = new byte[220];

        var packet = new DatagramPacket(buffer, buffer.length);

        while (true) {
            client.receive(packet);

            paymentSummaryWaiter.completeResponse(buildPayload(packet.getData(), packet.getLength()));
        }
    }

    private String buildPayload(byte[] event, int length) {
        return new String(event, 0, length, StandardCharsets.UTF_8);
    }
}
