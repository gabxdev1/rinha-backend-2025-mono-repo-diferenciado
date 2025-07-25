package br.com.gabxdev.service;

import br.com.gabxdev.client.LoadBalanceClient;
import br.com.gabxdev.mapper.JsonParse;
import br.com.gabxdev.middleware.PaymentMiddleware;
import br.com.gabxdev.repository.InMemoryPaymentDatabase;
import br.com.gabxdev.response.PaymentSummaryGetResponse;
import br.com.gabxdev.ws.Event;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

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

    private final LoadBalanceClient loadBalanceClient;

    private final InMemoryPaymentDatabase paymentRepository;

    private final PaymentMiddleware paymentMiddleware;

    private final ExecutorService pool = Executors.newCachedThreadPool(Thread.ofVirtual().factory());

    private final ArrayBlockingQueue<PaymentSummaryGetResponse> paymentsSummary = new ArrayBlockingQueue<>(1);

    public PaymentService(LoadBalanceClient loadBalanceClient, InMemoryPaymentDatabase paymentRepository, PaymentMiddleware paymentMiddleware) {
        this.loadBalanceClient = loadBalanceClient;
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

    public void getPaymentSummary(String payload) {
        var instants = payload.split("@");
        var from = parseInstant(instants[0]);
        var to = parseInstant(instants[1]);

        CompletableFuture.runAsync(() -> {
            paymentsSummary.offer(paymentMiddleware.syncPaymentSummary(from, to));
        }, pool);

        var paymentSummary2 = internalGetPaymentSummary(from, to);
        var paymentSummary1 = takeSummary();

        sendSummary(PaymentMiddleware.mergeSummary(paymentSummary1, paymentSummary2));
    }

    private PaymentSummaryGetResponse  takeSummary() {
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

    private void sendSummary(PaymentSummaryGetResponse response) {
        var payload = JsonParse.parseToJsonPaymentSummary(response);

        loadBalanceClient.sendEventLb(payload.getBytes(StandardCharsets.UTF_8));
    }

    public void purgePayments() {
        paymentRepository.deleteAll();

        paymentMiddleware.purgePayments();
    }

    public void purgePaymentsInternal() {
        paymentRepository.deleteAll();
    }
}
