package br.com.gabxdev.handler;

import br.com.gabxdev.config.ServerConfig;
import br.com.gabxdev.mapper.PaymentMapper;
import br.com.gabxdev.worker.PaymentWorker;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class ReceivePaymentHandler implements HttpHandler {

    private final ExecutorService threadPool = ServerConfig.getInstance().getWorkersThreadPool();

    private final PaymentWorker paymentWorker = PaymentWorker.getInstance();

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
                paymentWorker.enqueue(PaymentMapper.toPaymentInternal(payload));
            }, threadPool);
        });
    }

}
