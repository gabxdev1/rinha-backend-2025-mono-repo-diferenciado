package br.com.gabxdev.router;

import br.com.gabxdev.config.SocketInternalConfig;
import br.com.gabxdev.mapper.PaymentMapper;
import br.com.gabxdev.middleware.PaymentSummaryWaiter;
import br.com.gabxdev.model.Event;
import br.com.gabxdev.properties.ApplicationProperties;
import br.com.gabxdev.properties.PropertiesKey;
import br.com.gabxdev.service.PaymentService;
import br.com.gabxdev.worker.PaymentWorker;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public final class ApiRouter {

    private final static ApiRouter INSTANCE = new ApiRouter();

    private final PaymentWorker paymentWorker = PaymentWorker.getInstance();

    private final PaymentService paymentService = PaymentService.getInstance();

    private final PaymentSummaryWaiter paymentSummaryWaiter = PaymentSummaryWaiter.getInstance();

    private final DatagramSocket datagramSocketExternal = SocketInternalConfig.getInstance().getDatagramSocket();

    private ApiRouter() {
        start();
    }

    public static ApiRouter getInstance() {
        return INSTANCE;
    }

    private void start() {
        Thread.startVirtualThread(() -> {
            try {
                handleEvents();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void handleEvents() throws IOException {
        while (true) {
            var buffer = new byte[210];

            var datagramPacket = new DatagramPacket(buffer, buffer.length);

            datagramSocketExternal.receive(datagramPacket);

            mapperEvent(datagramPacket.getData());
        }
    }

    private void mapperEvent(byte[] data) {
        var event = Event.parseEvent(new String(data, StandardCharsets.UTF_8).trim());

        routerEvent(event);
    }

    private void routerEvent(Event event) {
        switch (event.getType()) {
            case PAYMENT_POST -> paymentWorker.enqueue(PaymentMapper.toPaymentExternal(event.getPayload()));
            case PAYMENT_SUMMARY -> paymentService.paymentSummaryToMerge(event.getPayload());
            case PAYMENT_SUMMARY_MERGE -> paymentSummaryWaiter.completeResponse(event.getPayload());
            case PURGER -> paymentService.purgePaymentsInternal();
        }
    }
}
