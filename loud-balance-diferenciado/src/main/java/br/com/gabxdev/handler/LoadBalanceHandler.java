package br.com.gabxdev.handler;

import br.com.gabxdev.lb.ResponseWaiter;
import br.com.gabxdev.mapper.EventMapper;
import br.com.gabxdev.service.LoadBalanceService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeoutException;

@Component
public class LoadBalanceHandler {

    private final Mono<ServerResponse> serverResponseOk = ServerResponse.ok().build();

    private final LoadBalanceService loadBalanceService;

    private final ResponseWaiter responseWaiter;

    private final EventMapper eventMapper;

    public LoadBalanceHandler(LoadBalanceService loadBalanceService, ResponseWaiter responseWaiter, EventMapper eventMapper) {
        this.loadBalanceService = loadBalanceService;
        this.responseWaiter = responseWaiter;
        this.eventMapper = eventMapper;
    }

    public Mono<ServerResponse> receivePayment(ServerRequest request) {
        return request.bodyToMono(String.class)
                .doOnNext(loadBalanceService::receivePaymentHandler)
                .then(serverResponseOk);
    }

    public Mono<ServerResponse> purgePayments(ServerRequest request) {
        loadBalanceService.purgePaymentsHandler();

        return serverResponseOk;
    }

    public Mono<ServerResponse> paymentSummary(ServerRequest request) {
        var from = request.queryParam("from").orElse("");
        var to = request.queryParam("to").orElse("");

        var eventPayload = eventMapper.toPaymentSummaryGetRequest(from, to);

        loadBalanceService.paymentSummaryHandler(eventPayload);

        return responseWaiter.awaitResponse()
                .flatMap(this::buildServerResponse)
                .onErrorResume(TimeoutException.class, e ->
                        ServerResponse.status(504)
                                .contentType(MediaType.TEXT_PLAIN)
                                .bodyValue("Timeout")
                );
    }

    private Mono<ServerResponse> buildServerResponse(String body) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body);
    }
}
