package br.com.gabxdev.service;

import br.com.gabxdev.config.DatagramSocketConfig;
import br.com.gabxdev.config.DatagramSocketExternalConfig;
import br.com.gabxdev.dto.Event;
import br.com.gabxdev.dto.EventType;
import br.com.gabxdev.mapper.EventMapper;
import br.com.gabxdev.mapper.JsonParse;
import br.com.gabxdev.middleware.PaymentMiddleware;
import br.com.gabxdev.middleware.PaymentSummaryWaiter;
import br.com.gabxdev.repository.InMemoryPaymentDatabase;
import br.com.gabxdev.response.PaymentSummaryGetResponse;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static br.com.gabxdev.mapper.JsonParse.parseInstant;

public final class PaymentService {

    private static final PaymentService INSTANCE = new PaymentService();

    private final DatagramSocket datagramSocket = DatagramSocketConfig.getInstance().getDatagramSocket();

    private final DatagramSocket datagramSocketExternal = DatagramSocketExternalConfig.getInstance().getDatagramSocket();

    private final InMemoryPaymentDatabase paymentRepository = InMemoryPaymentDatabase.getInstance();

    private final PaymentMiddleware paymentMiddleware = PaymentMiddleware.getInstance();

    private final ExecutorService pool = Executors.newCachedThreadPool(Thread.ofVirtual().factory());

    private final PaymentSummaryWaiter paymentSummaryWaiter = PaymentSummaryWaiter.getInstance();

    private PaymentService() {
    }

    public static PaymentService getInstance() {
        return INSTANCE;
    }

    public void paymentSummaryToMerge(String eventJson, InetAddress addressLb, int portLb) {
        var instants = eventJson.split("@");
        var from = parseInstant(instants[0]);
        var to = parseInstant(instants[1]);

        PaymentSummaryGetResponse paymentSummary;

        if (from.atZone(ZoneOffset.UTC).getYear() == 2000) {
            paymentSummary = paymentRepository.getTotalSummary();
        } else {
            paymentSummary = paymentRepository.getSummaryByTimeRange(from, to);
        }

        sendSummary(paymentSummary, addressLb, portLb, datagramSocketExternal, true);
    }

    public void getPaymentSummary(String payload, InetAddress addressLb, int portLb) {
        var instants = payload.split("@");
        var from = parseInstant(instants[0]);
        var to = parseInstant(instants[1]);

        CompletableFuture.runAsync(() -> {
            paymentMiddleware.syncPaymentSummary(instants[0], instants[1]);
        }, pool);

        var paymentSummary2 = internalGetPaymentSummary(from, to);
        var paymentSummary1 = paymentSummaryWaiter.awaitResponse();

        sendSummary(PaymentMiddleware.mergeSummary(paymentSummary1, paymentSummary2),
                addressLb,
                portLb,
                datagramSocket,
                false);
    }

    private PaymentSummaryGetResponse internalGetPaymentSummary(Instant from, Instant to) {
        if (from.atZone(ZoneOffset.UTC).getYear() == 2000) {
            return paymentRepository.getTotalSummary();
        } else {
            return paymentRepository.getSummaryByTimeRange(from, to);
        }
    }

    private void sendSummary(PaymentSummaryGetResponse response,
                             InetAddress addressLb,
                             int portLb,
                             DatagramSocket datagramSocket,
                             boolean internal) {
        byte[] payload;

        if (internal) {
            payload = Event.buildEventDTO("id", EventType.PAYMENT_SUMMARY_MERGE.ordinal(),
                    JsonParse.parseToJsonPaymentSummaryInternal(response)).getBytes(StandardCharsets.UTF_8);
        } else {
            payload = JsonParse.parseToJsonPaymentSummary(response).getBytes(StandardCharsets.UTF_8);
        }

        var datagramPacket = new DatagramPacket(payload, payload.length, addressLb, portLb);

        try {
            datagramSocket.send(datagramPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void purgePayments() {
        paymentRepository.deleteAll();

        paymentMiddleware.purgePayments();
    }

    public void purgePaymentsInternal() {
        paymentRepository.deleteAll();
    }
}
