package br.com.gabxdev.consumer;

import br.com.gabxdev.config.LoudBalanceChannelConfig;
import br.com.gabxdev.model.enums.EventType;
import br.com.gabxdev.properties.ApplicationProperties;
import br.com.gabxdev.properties.PropertiesKey;
import br.com.gabxdev.service.PaymentService;

import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;

public class EventConsumer {

    private final static EventConsumer INSTANCE = new EventConsumer();

    private final PaymentService paymentService = PaymentService.getInstance();

    private EventConsumer() {
        var properties = ApplicationProperties.getInstance();

        var isMaster = Boolean.parseBoolean(properties.getProperty(PropertiesKey.API_MASTER));

        if (isMaster) {
            Thread.startVirtualThread(() -> {
                try {
                    handlerEvent();
                } catch (IOException e) {
                    System.out.println("Error: handlerEvent " + e.getMessage());
                }
            });
        }
    }

    public static EventConsumer getInstance() {
        return INSTANCE;
    }

    private void handlerEvent() throws IOException {
        var socket = LoudBalanceChannelConfig.getInstance().getSocket();

        var buffer = new byte[60];

        var packet = new DatagramPacket(buffer, buffer.length);

        while (true) {
            socket.receive(packet);

            routerEvent(new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8));
        }
    }

    private void routerEvent(String event) {
        var key = event.charAt(event.length() - 1);

        if (EventType.valueOf(key).equals(EventType.PURGE)) {
            paymentService.purgePayments();

            return;
        }

        if (EventType.valueOf(key).equals(EventType.PAYMENT_SUMMARY)) {
            paymentService.getPaymentSummary(event.replace("a", ""));
        }
    }
}
