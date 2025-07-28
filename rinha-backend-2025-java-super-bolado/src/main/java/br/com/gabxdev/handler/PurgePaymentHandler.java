package br.com.gabxdev.handler;

import br.com.gabxdev.config.ServerConfig;
import br.com.gabxdev.service.PaymentService;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class PurgePaymentHandler implements HttpHandler {

    private final PaymentService paymentService = PaymentService.getInstance();

    private final ExecutorService threadPool = ServerConfig.getInstance().getWorkersThreadPool();

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        var method = exchange.getRequestMethod().toString();

        if (method.equals("POST")) {
            handlePurgePayment(exchange);
        }
    }

    private void handlePurgePayment(HttpServerExchange exchange) {
        CompletableFuture.runAsync(paymentService::purgePayments, threadPool);
    }
}
