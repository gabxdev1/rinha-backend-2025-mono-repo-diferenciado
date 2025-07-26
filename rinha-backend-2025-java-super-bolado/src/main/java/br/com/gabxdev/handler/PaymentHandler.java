package br.com.gabxdev.handler;

import br.com.gabxdev.config.LoadBalanceClient;
import br.com.gabxdev.mapper.JsonParse;
import br.com.gabxdev.model.Payment;
import br.com.gabxdev.service.PaymentService;
import br.com.gabxdev.worker.PaymentWorker;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.SocketAddress;

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
        paymentService.purgePayments();
    }

    public void paymentSummary(String payload, InetAddress addressLb, int portLb) {
        paymentService.getPaymentSummary(payload, addressLb, portLb);
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
