package br.com.gabxdev.handler;

import br.com.gabxdev.client.UdpClient;
import br.com.gabxdev.config.SocketInternalConfig;
import br.com.gabxdev.lb.LoudBalance;
import br.com.gabxdev.mapper.EventMapper;
import br.com.gabxdev.mapper.PaymentMapper;
import br.com.gabxdev.service.PaymentService;
import br.com.gabxdev.worker.PaymentWorker;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

@Component
public class PaymentHandler {

    private final Mono<ServerResponse> serverResponseOk = ServerResponse.ok().build();

    private final LoudBalance loudBalance = LoudBalance.getInstance();

    private final PaymentWorker paymentWorker = PaymentWorker.getInstance();

    private final PaymentService paymentService = PaymentService.getInstance();

    private final UdpClient udpClient = UdpClient.getInstance();

    private final DatagramSocket socket = SocketInternalConfig.getInstance().getDatagramSocket();

    public PaymentHandler() {
    }

    public Mono<ServerResponse> receivePayment(ServerRequest request) {
        return request.bodyToMono(String.class)
                .doOnNext((payload) -> {
                    if (loudBalance.selectBackEnd() == 1) {
                        processPaymentInternal(payload);
                    } else {
                        processPaymentExternal(payload);
                    }
                })
                .then(serverResponseOk);
    }

    private void processPaymentInternal(String payload) {
        paymentWorker.enqueue(PaymentMapper.toPaymentInternal(payload));
    }

    private void processPaymentExternal(String payload) {
        var body = EventMapper.toPaymentPostRequest(payload)
                .getBytes(StandardCharsets.UTF_8);

        udpClient.send(new DatagramPacket(body, body.length), socket);
    }

    public Mono<ServerResponse> purgePayments(ServerRequest request) {
        paymentService.purgePayments();

        return serverResponseOk;
    }

    public Mono<ServerResponse> paymentSummary(ServerRequest request) {
        var from = request.queryParam("from").orElse("");
        var to = request.queryParam("to").orElse("");

        var paymentSummaryResponse = paymentService.getPaymentSummary(from, to);

        return buildServerResponse(paymentSummaryResponse);
    }

    private Mono<ServerResponse> buildServerResponse(String body) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body);
    }
}
