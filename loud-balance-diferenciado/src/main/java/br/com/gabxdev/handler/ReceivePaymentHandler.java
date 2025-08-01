package br.com.gabxdev.handler;

import br.com.gabxdev.config.ServerConfig;
import br.com.gabxdev.service.LoadBalanceService;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class ReceivePaymentHandler implements HttpHandler {
    private final LoadBalanceService loadBalanceService = LoadBalanceService.getInstance();

    private final ExecutorService threadPool = ServerConfig.getInstance().getWorkersThreadPool();

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        var method = exchange.getRequestMethod().toString();

        if (method.equals("POST")) {
            handleReceivePayment(exchange);
        }
    }

    private void handleReceivePayment(HttpServerExchange exchange) {
        exchange.getRequestReceiver().receiveFullBytes((httpServerExchange, payload) -> {
            CompletableFuture.runAsync(() -> {
                loadBalanceService.receivePaymentHandler(new String(payload, StandardCharsets.UTF_8));
            }, threadPool);
        });
    }
}
