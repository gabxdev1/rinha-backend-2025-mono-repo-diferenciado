package br.com.gabxdev.repository;

import br.com.gabxdev.model.Payment;
import br.com.gabxdev.model.enums.PaymentProcessorType;
import br.com.gabxdev.response.PaymentSummary;
import br.com.gabxdev.response.PaymentSummaryGetResponse;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public final class InMemoryPaymentDatabase {

    private final static InMemoryPaymentDatabase INSTANCE = new InMemoryPaymentDatabase();

    private final ConcurrentLinkedQueue<Payment> payments = new ConcurrentLinkedQueue<>();

    private InMemoryPaymentDatabase() {
    }

    public static InMemoryPaymentDatabase getInstance() {
        return INSTANCE;
    }

    public void save(Payment payment) {
        payments.offer(payment);
    }

    public void deleteAll() {
        payments.clear();
    }

    public PaymentSummaryGetResponse getTotalSummary() {
        var snapshot = getSnapshot();

        var grouped = snapshot.stream()
                .collect(Collectors.groupingBy(p -> p.type));

        return toPaymentSummaryGetResponse(grouped);
    }

    public PaymentSummaryGetResponse getSummaryByTimeRange(Instant from, Instant to) {
        var snapshot = getSnapshot();

        var grouped = snapshot.stream()
                .filter(p -> !p.getRequestedAt().isBefore(from) && !p.getRequestedAt().isAfter(to))
                .collect(Collectors.groupingBy(p -> p.type));

        return toPaymentSummaryGetResponse(grouped);
    }

    private PaymentSummaryGetResponse toPaymentSummaryGetResponse(Map<PaymentProcessorType, List<Payment>> grouped) {
        var defaultSummary = summarize(PaymentProcessorType.DEFAULT, grouped.get(PaymentProcessorType.DEFAULT));
        var fallbackSummary = summarize(PaymentProcessorType.FALLBACK, grouped.get(PaymentProcessorType.FALLBACK));

        return new PaymentSummaryGetResponse(defaultSummary, fallbackSummary);
    }

    private PaymentSummary summarize(PaymentProcessorType type, List<Payment> payments) {
        if (payments == null || payments.isEmpty()) {
            return new PaymentSummary(type, 0, BigDecimal.ZERO);
        }

        var count = payments.size();
        var total = payments.getFirst().getAmount().multiply(BigDecimal.valueOf(count));

        return new PaymentSummary(type, count, total);
    }

    private List<Payment> getSnapshot() {
        return new ArrayList<>(payments);
    }
}