package br.com.gabxdev.consumer;

import br.com.gabxdev.config.ApiSockerInternalConfig;
import br.com.gabxdev.middleware.PaymentSummaryWaiter;
import br.com.gabxdev.model.Event;
import br.com.gabxdev.service.PaymentService;

import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;

import static br.com.gabxdev.model.enums.EventType.*;

public class ApiInternalConsumer {

    private final static ApiInternalConsumer INSTANCE = new ApiInternalConsumer();

    private final PaymentService paymentService = PaymentService.getInstance();

    private final PaymentSummaryWaiter paymentSummaryWaiter = PaymentSummaryWaiter.getInstance();

    private ApiInternalConsumer() {
        Thread.startVirtualThread(() -> {
            try {
                handleEvents();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static ApiInternalConsumer getInstance() {
        return INSTANCE;
    }

    private void handleEvents() throws IOException {
        var socket = ApiSockerInternalConfig.getInstance().getDatagramSocket();

        var buffer = new byte[100];
        var packet = new DatagramPacket(buffer, buffer.length);

        while (true) {
            socket.receive(packet);

            mapperEvent(packet.getData(), packet.getLength());
        }
    }

    private void mapperEvent(byte[] data, int length) {
        var event = Event.parseEvent(new String(data, 0, length, StandardCharsets.UTF_8));

        routerEvent(event);
    }

    private void routerEvent(Event event) {
        if (event.getType().equals(PAYMENT_SUMMARY)) {
            paymentService.paymentSummaryToMerge(event.getPayload());

            return;
        }

        if (event.getType().equals(PAYMENT_SUMMARY_MERGE)) {
            paymentSummaryWaiter.completeResponse(event.getPayload());

            return;
        }

        if (event.getType().equals(PURGE)) {
            paymentService.purgePaymentsInternal();
        }
    }
}
