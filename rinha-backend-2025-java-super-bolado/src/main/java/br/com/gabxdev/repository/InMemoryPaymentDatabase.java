package br.com.gabxdev.repository;

import br.com.gabxdev.model.Payment;
import br.com.gabxdev.model.enums.PaymentProcessorType;
import br.com.gabxdev.response.PaymentSummary;
import br.com.gabxdev.response.PaymentSummaryGetResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public final class InMemoryPaymentDatabase {

    private final static InMemoryPaymentDatabase INSTANCE = new InMemoryPaymentDatabase();

    private final ConcurrentLinkedQueue<Payment> paymentsDefault = new ConcurrentLinkedQueue<>();

    private final ConcurrentLinkedQueue<Payment> paymentsFallback = new ConcurrentLinkedQueue<>();

    private InMemoryPaymentDatabase() {
    }

    public static InMemoryPaymentDatabase getInstance() {
        return INSTANCE;
    }

    public void save(Payment payment) {
        if (payment.type.equals(PaymentProcessorType.DEFAULT)) {
            paymentsDefault.offer(payment);
        } else {
            paymentsFallback.offer(payment);
        }
    }

    public void deleteAll() {
        paymentsDefault.clear();
        paymentsFallback.clear();
    }

    public PaymentSummaryGetResponse getTotalSummary() {
        var summaryDefault = summarize(PaymentProcessorType.DEFAULT, getSnapshotDefault());
        var summaryFallback = summarize(PaymentProcessorType.FALLBACK, getSnapshotFallback());

        return new PaymentSummaryGetResponse(summaryDefault, summaryFallback);
    }

    public PaymentSummaryGetResponse getSummaryByTimeRange(long from, long to) {
        var snapshotDefault = getSnapshotDefault();
        var snapshotFallback = getSnapshotFallback();

        var defaultList = snapshotDefault.stream()
                .filter(p -> p.getRequestedAt() >= from && p.getRequestedAt() <= to)
                .toList();

        var fallbackList = snapshotFallback.stream()
                .filter(p -> p.getRequestedAt() >= from && p.getRequestedAt() <= to)
                .toList();

        var summaryDefault = summarize(PaymentProcessorType.DEFAULT, defaultList);
        var summaryFallback = summarize(PaymentProcessorType.FALLBACK, fallbackList);

        return new PaymentSummaryGetResponse(summaryDefault, summaryFallback);
    }

    private PaymentSummary summarize(PaymentProcessorType type, List<Payment> payments) {
        if (payments == null || payments.isEmpty()) {
            return new PaymentSummary(type, 0, BigDecimal.ZERO);
        }

        var count = payments.size();
        var total = payments.getFirst().getAmount().multiply(BigDecimal.valueOf(count));

        return new PaymentSummary(type, count, total);
    }

    private List<Payment> getSnapshotDefault() {
        return new ArrayList<>(paymentsDefault);
    }

    private List<Payment> getSnapshotFallback() {
        return new ArrayList<>(paymentsFallback);
    }
}