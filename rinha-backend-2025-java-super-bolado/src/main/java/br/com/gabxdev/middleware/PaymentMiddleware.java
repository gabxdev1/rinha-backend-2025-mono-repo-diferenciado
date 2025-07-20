package br.com.gabxdev.middleware;

import br.com.gabxdev.mapper.PaymentSummaryMapper;
import br.com.gabxdev.response.PaymentSummaryGetResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class PaymentMiddleware {

    private final AtomicReference<Sinks.One<PaymentSummaryGetResponse>> sinkRef = new AtomicReference<>();

    private final RestClient apiInternalClient;

    public PaymentMiddleware(RestClient apiInternalClient) {
        this.apiInternalClient = apiInternalClient;
    }

    public void purgePayments() {
        callBackEndToPurgePayments();
    }

    public Mono<PaymentSummaryGetResponse> syncPaymentSummary(Instant from, Instant to) {
        return Mono.fromCallable(() ->
                callBackEndSummary(from, to)).subscribeOn(Schedulers.boundedElastic());
    }

//    public Mono<Void> syncPaymentSummary(Instant from, Instant to) {
//        Sinks.One<PaymentSummaryGetResponse> sink = Sinks.one();
//        sinkRef.set(sink);
//
//        return Mono.fromRunnable(() -> {
//            var response = callBackEndSummary(from, to);
//            sink.tryEmitValue(response);
//        }).subscribeOn(Schedulers.boundedElastic()).then();
//    }

    public Mono<PaymentSummaryGetResponse> takeSummaryMerged(PaymentSummaryGetResponse current) {
        var sink = sinkRef.getAndSet(null);
        if (sink == null) {
            System.out.println("No sink available");
            return Mono.error(new IllegalStateException("Nenhuma resposta disponível"));
        }

        return sink.asMono()
                .map(remote -> mergeSummary(current, remote));
    }

    public static PaymentSummaryGetResponse mergeSummary(PaymentSummaryGetResponse summary1,
                                                   PaymentSummaryGetResponse summary2) {
        var api1TotalAmount1 = summary1.getDefaultApi().getTotalAmount();
        var api2TotalAmount1 = summary2.getDefaultApi().getTotalAmount();

        var api1TotalAmount2 = summary1.getFallbackApi().getTotalAmount();
        var api2TotalAmount2 = summary2.getFallbackApi().getTotalAmount();


        var api1TotalRequests1 = summary1.getDefaultApi().getTotalRequests();
        var api2TotalRequests1 = summary2.getDefaultApi().getTotalRequests();

        var api1TotalRequests2 = summary1.getFallbackApi().getTotalRequests();
        var api2TotalRequests2 = summary2.getFallbackApi().getTotalRequests();


        summary1.getDefaultApi().setTotalAmount(api1TotalAmount1.add(api2TotalAmount1));
        summary1.getFallbackApi().setTotalAmount(api1TotalAmount2.add(api2TotalAmount2));


        summary1.getDefaultApi().setTotalRequests(api1TotalRequests1 + api2TotalRequests1);
        summary1.getFallbackApi().setTotalRequests(api1TotalRequests2 + api2TotalRequests2);

        return summary1;
    }

    private void callBackEndToPurgePayments() {
        apiInternalClient
                .post()
                .uri("/internal/purge-payments")
                .retrieve()
                .toBodilessEntity();
    }

    private PaymentSummaryGetResponse callBackEndSummary(Instant from, Instant to) {
        var response = apiInternalClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/internal/payments-summary")
                        .queryParam("from", from)
                        .queryParam("to", to)
                        .build())
                .retrieve()
                .body(String.class);

        return PaymentSummaryMapper.toPaymentSummary(response);
    }
}
