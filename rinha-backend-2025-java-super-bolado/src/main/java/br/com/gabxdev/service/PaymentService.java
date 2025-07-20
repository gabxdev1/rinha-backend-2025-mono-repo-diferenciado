package br.com.gabxdev.service;

import br.com.gabxdev.mapper.JsonParse;
import br.com.gabxdev.middleware.PaymentMiddleware;
import br.com.gabxdev.repository.InMemoryPaymentDatabase;
import br.com.gabxdev.response.PaymentSummaryGetResponse;
import br.com.gabxdev.ws.Event;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Instant;
import java.time.ZoneOffset;

import static br.com.gabxdev.mapper.JsonParse.parseInstant;

@Service
public class PaymentService {

    private final InMemoryPaymentDatabase paymentRepository;

    private final PaymentMiddleware paymentMiddleware;

    public PaymentService(InMemoryPaymentDatabase paymentRepository, PaymentMiddleware paymentMiddleware) {
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

    public void getPaymentSummary(Event event, Sinks.Many<String> sink) {
        var instants = event.getPayload().split("@");
        var from = parseInstant(instants[0]);
        var to = parseInstant(instants[1]);

        Mono.zip(paymentMiddleware.syncPaymentSummary(from, to), internalGetPaymentSummary(from, to))
                .map(tuple ->
                        PaymentMiddleware.mergeSummary(tuple.getT1(), tuple.getT2()))
                .subscribe(summary -> sendSummary(event, summary, sink));
    }

    private Mono<PaymentSummaryGetResponse> internalGetPaymentSummary(Instant from, Instant to) {
        return Mono.fromSupplier(() -> {
            if (from.atZone(ZoneOffset.UTC).getYear() == 2000) {
                return paymentRepository.getTotalSummary();
            } else {
                return paymentRepository.getSummaryByTimeRange(from, to);
            }
        });
    }

    private void sendSummary(Event event, PaymentSummaryGetResponse response, Sinks.Many<String> sink) {
        var payload = JsonParse.parseToJsonPaymentSummary(response);
        event.setPayload(payload);

        var message = Event.buildEventDTO(event);
        sink.tryEmitNext(message);
    }

    public void purgePayments() {
        paymentRepository.deleteAll();

        paymentMiddleware.purgePayments();
    }

    public void purgePaymentsInternal() {
        paymentRepository.deleteAll();
    }
}
