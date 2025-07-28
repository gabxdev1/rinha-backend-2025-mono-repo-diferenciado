package br.com.gabxdev.handler;

import br.com.gabxdev.config.DatagramSocketExternalConfig;
import br.com.gabxdev.model.Event;
import br.com.gabxdev.middleware.PaymentSummaryWaiter;
import br.com.gabxdev.model.Payment;
import br.com.gabxdev.service.PaymentService;
import br.com.gabxdev.worker.PaymentWorker;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public final class PaymentHandler {

    private static final PaymentHandler INSTANCE = new PaymentHandler();

    private final PaymentService paymentService = PaymentService.getInstance();

    private final PaymentWorker paymentWorker = PaymentWorker.getInstance();

    private final PaymentSummaryWaiter paymentSummaryWaiter = PaymentSummaryWaiter.getInstance();

    private final DatagramSocket datagramSocketExternal = DatagramSocketExternalConfig.getInstance().getDatagramSocket();


    private PaymentHandler() {
        start();
    }

    public static PaymentHandler getInstance() {
        return INSTANCE;
    }

    public void receivePayment(Payment payment) {
        paymentWorker.enqueue(payment);
    }

    public void purgePayments() {
        paymentService.purgePayments();
    }

    public void paymentSummary(String payload, InetAddress addressLb, int portLb) {
        paymentService.getPaymentSummary(payload, addressLb, portLb);
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
            var buffer = new byte[60];

            var datagramPacket = new DatagramPacket(buffer, buffer.length);

            datagramSocketExternal.receive(datagramPacket);

            var data = new String(datagramPacket.getData(), StandardCharsets.UTF_8).trim();

            Thread.startVirtualThread(() ->
                    processEvent(data, datagramPacket.getAddress(), datagramPacket.getPort()));
        }
    }

    private void processEvent(String eventJson, InetAddress addressLb, int portLb) {
        var event = Event.parseEvent(eventJson);

        switch (event.getType()) {
            case PAYMENT_SUMMARY -> paymentService.paymentSummaryToMerge(event.getPayload(), addressLb, portLb);
            case PAYMENT_SUMMARY_MERGE -> paymentSummaryWaiter.completeResponse(event.getPayload());
            case PURGER -> paymentService.purgePaymentsInternal();
        }
    }
}
