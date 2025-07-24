package br.com.gabxdev.handler;

import br.com.gabxdev.mapper.JsonParse;
import br.com.gabxdev.model.Payment;
import br.com.gabxdev.service.PaymentService;
import br.com.gabxdev.worker.PaymentWorker;
import br.com.gabxdev.ws.Event;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

@Component
public class PaymentHandler {

    private final PaymentService paymentService;

    private final PaymentWorker paymentWorker;

    private final Mono<ServerResponse> serverResponseOk = ServerResponse.ok().build();

    public PaymentHandler(PaymentService paymentService, PaymentWorker paymentWorker) {
        this.paymentService = paymentService;
        this.paymentWorker = paymentWorker;
    }

    public void receivePayment(Payment payment) {
        paymentWorker.enqueue(payment);
    }

    public void purgePayments() {
        Mono.fromRunnable(paymentService::purgePayments)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

    public void paymentSummary(Event event, Sinks.Many<String> sink) {
        paymentService.getPaymentSummary(event, sink);
    }


    public Mono<ServerResponse> purgePaymentsInternal(ServerRequest request) {
        paymentService.purgePaymentsInternal();

        return serverResponseOk;
    }

    public Mono<ServerResponse> paymentSummaryInternal(ServerRequest request) {
        var from = request.queryParam("from").orElse(null);
        var to = request.queryParam("to").orElse(null);


        var paymentSummary = paymentService.paymentSummaryToMerge(from, to);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(JsonParse.parseToJsonPaymentSummaryInternal(paymentSummary));
    }
}
