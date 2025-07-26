package br.com.gabxdev.service;

import br.com.gabxdev.config.LoadBalanceClient;
import br.com.gabxdev.mapper.JsonParse;
import br.com.gabxdev.middleware.PaymentMiddleware;
import br.com.gabxdev.repository.InMemoryPaymentDatabase;
import br.com.gabxdev.response.PaymentSummaryGetResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static br.com.gabxdev.mapper.JsonParse.parseInstant;

@Service
public class PaymentService {

    private final DatagramSocket datagramSocket;

    private final InMemoryPaymentDatabase paymentRepository;

    private final PaymentMiddleware paymentMiddleware;

    private final ExecutorService pool = Executors.newCachedThreadPool(Thread.ofVirtual().factory());

    private final ArrayBlockingQueue<PaymentSummaryGetResponse> paymentsSummary = new ArrayBlockingQueue<>(1);

    public PaymentService(DatagramSocket datagramSocket, InMemoryPaymentDatabase paymentRepository, PaymentMiddleware paymentMiddleware) {
        this.datagramSocket = datagramSocket;
        this.paymentRepository = paymentRepository;
        this.paymentMiddleware = paymentMiddleware;
    }

    public PaymentSummaryGetResponse paymentSummaryToMerge(String fromS, String toS) {
        var from = parseInstant(fromS);
        var to = parseInstant(toS);

        if (from.atZone(ZoneOffset.UTC).getYear() == 2000) {
            return paymentRepository.getTotalSummary();
        } else {
            return paymentRepository.getSummaryByTimeRange(from, to);
        }
    }

    public void getPaymentSummary(String payload, LoadBalanceClient client) {
        var instants = payload.split("@");
        var from = parseInstant(instants[0]);
        var to = parseInstant(instants[1]);

        CompletableFuture.runAsync(() -> {
            paymentsSummary.offer(paymentMiddleware.syncPaymentSummary(from, to));
        }, pool);

        var paymentSummary2 = internalGetPaymentSummary(from, to);
        var paymentSummary1 = takeSummary();

        sendSummary(PaymentMiddleware.mergeSummary(paymentSummary1, paymentSummary2), client);
    }

    private PaymentSummaryGetResponse takeSummary() {
        try {
            return paymentsSummary.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private PaymentSummaryGetResponse internalGetPaymentSummary(Instant from, Instant to) {
        if (from.atZone(ZoneOffset.UTC).getYear() == 2000) {
            return paymentRepository.getTotalSummary();
        } else {
            return paymentRepository.getSummaryByTimeRange(from, to);
        }
    }

    private void sendSummary(PaymentSummaryGetResponse response, LoadBalanceClient client) {
        var payload = JsonParse.parseToJsonPaymentSummary(response).getBytes(StandardCharsets.UTF_8);

        System.out.println(payload.length);

        var datagramPacket = new DatagramPacket(payload, payload.length, client.ip(), client.port());

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
